package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.model.Rating;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.UserService;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.FavoriteService;
import at.fhtw.swen1.mrp.dto.MediaDTO;
import at.fhtw.swen1.mrp.dto.RatingDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

public class MediaHandler implements HttpHandler {
    private final MediaService mediaService;
    private final UserService userService;
    private final RatingService ratingService;
    private final FavoriteService favoriteService;
    private final ObjectMapper objectMapper;

    public MediaHandler(MediaService mediaService, UserService userService, RatingService ratingService, FavoriteService favoriteService, ObjectMapper objectMapper) {
        this.mediaService = mediaService;
        this.userService = userService;
        this.ratingService = ratingService;
        this.favoriteService = favoriteService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("/api/media".equals(path)) {
                if ("GET".equalsIgnoreCase(method)) {
                    handleGetMedia(exchange);
                } else if ("POST".equalsIgnoreCase(method)) {
                    handlePostMedia(exchange);
                } else {
                    sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
                }
            } else if (path.matches("^/api/media/\\d+$")) {
                 // /api/media/{id}
                 if ("GET".equalsIgnoreCase(method)) {
                     handleGetMediaById(exchange, path);
                 } else if ("PUT".equalsIgnoreCase(method)) {
                     handleUpdateMedia(exchange, path);
                 } else if ("DELETE".equalsIgnoreCase(method)) {
                     handleDeleteMedia(exchange, path);
                 } else {
                     sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
                 }
            } else if (path.matches("^/api/media/\\d+/rate$")) {
                // /api/media/{id}/rate
                if ("POST".equalsIgnoreCase(method)) {
                    handleRateMedia(exchange, path);
                } else {
                    sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
                }
            } else if (path.matches("^/api/media/\\d+/favorite$")) {
                 if ("POST".equalsIgnoreCase(method)) {
                     handleFavoriteMedia(exchange, path, true);
                 } else if ("DELETE".equalsIgnoreCase(method)) {
                     handleFavoriteMedia(exchange, path, false);
                 } else {
                     sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
                 }
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Not Found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"Internal Server Error: " + e.getMessage() + "\"}");
        }
    }
    
    private void handleFavoriteMedia(HttpExchange exchange, String path, boolean isAdd) throws IOException {
        User user = authenticate(exchange);
        if (user == null) return;
        
        int mediaId = parseIdFromPath(path, 3);
        
        if (isAdd) {
            favoriteService.addFavorite(user.getId(), mediaId);
            sendResponse(exchange, 201, "{\"message\": \"Added to favorites\"}");
        } else {
            favoriteService.removeFavorite(user.getId(), mediaId);
            sendResponse(exchange, 200, "{\"message\": \"Removed from favorites\"}");
        }
    }

    private void handleGetMediaById(HttpExchange exchange, String path) throws IOException {
        int mediaId = parseIdFromPath(path, 3);
        Optional<Media> media = mediaService.getMediaById(mediaId);
        
        if (media.isPresent()) {
            String response = objectMapper.writeValueAsString(media.get());
            sendResponse(exchange, 200, response);
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Media not found\"}");
        }
    }

    private void handleUpdateMedia(HttpExchange exchange, String path) throws IOException {
        User user = authenticate(exchange);
        if (user == null) return;
        
        int mediaId = parseIdFromPath(path, 3);
        InputStream requestBody = exchange.getRequestBody();
        MediaDTO mediaDTO = objectMapper.readValue(requestBody, MediaDTO.class);
        
        Media mediaUpdates = new Media.Builder()
                .title(mediaDTO.getTitle())
                .description(mediaDTO.getDescription())
                .mediaType(mediaDTO.getMediaType())
                .releaseYear(mediaDTO.getReleaseYear())
                .ageRestriction(mediaDTO.getAgeRestriction())
                .genres(mediaDTO.getGenres())
                .build();
                
        try {
            Media updated = mediaService.updateMedia(mediaId, mediaUpdates, user.getId());
            String response = objectMapper.writeValueAsString(updated);
            sendResponse(exchange, 200, response);
        } catch (IllegalArgumentException e) {
             sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleDeleteMedia(HttpExchange exchange, String path) throws IOException {
        User user = authenticate(exchange);
        if (user == null) return;
        
        int mediaId = parseIdFromPath(path, 3);
        
        try {
            mediaService.deleteMedia(mediaId, user.getId());
            sendResponse(exchange, 204, "");
        } catch (IllegalArgumentException e) {
             sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private int parseIdFromPath(String path, int index) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[index]);
    }
    
    private User authenticate(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        User user = null;
        if (authHeader != null) {
            user = userService.getUserByToken(authHeader).orElse(null);
        }
        if (user == null) {
            sendResponse(exchange, 401, "{\"error\": \"Unauthorized\"}");
            return null;
        }
        return user;
    }

    private void handleRateMedia(HttpExchange exchange, String path) throws IOException {
        // Authenticate
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        User user = null;
        if (authHeader != null) {
            user = userService.getUserByToken(authHeader).orElse(null);
        }
        if (user == null) {
            sendResponse(exchange, 401, "{\"error\": \"Unauthorized\"}");
            return;
        }

        // Parse ID
        String[] parts = path.split("/");
        // /api/media/{id}/rate
        // parts[0]="", [1]="api", [2]="media", [3]="{id}", [4]="rate"
        if (parts.length < 5) {
             sendResponse(exchange, 400, "{\"error\": \"Invalid path\"}");
             return;
        }
        int mediaId;
        try {
            mediaId = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
             sendResponse(exchange, 400, "{\"error\": \"Invalid Media ID\"}");
             return;
        }

        // Parse Body
        InputStream requestBody = exchange.getRequestBody();
        RatingDTO ratingDTO = objectMapper.readValue(requestBody, RatingDTO.class);
        
        try {
            Rating rating = ratingService.rateMedia(user.getId(), mediaId, ratingDTO.getStars(), ratingDTO.getComment());
            String response = objectMapper.writeValueAsString(rating);
            sendResponse(exchange, 201, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
             sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleGetMedia(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);

        String title = params.get("title");
        String mediaType = params.get("mediaType");
        String genre = params.get("genre");
        Integer releaseYear = params.containsKey("releaseYear") ? Integer.parseInt(params.get("releaseYear")) : null;
        Integer ageRestriction = params.containsKey("ageRestriction") ? Integer.parseInt(params.get("ageRestriction")) : null;
        Double minRating = params.containsKey("rating") ? Double.parseDouble(params.get("rating")) : null; // "rating" from query usually implies min rating

        List<Media> mediaList = mediaService.getMedia(title, mediaType, releaseYear, ageRestriction, genre, minRating);
        
        String response = objectMapper.writeValueAsString(mediaList);
        sendResponse(exchange, 200, response);
    }

    private void handlePostMedia(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        User user = null;
        if (authHeader != null) {
            user = userService.getUserByToken(authHeader).orElse(null);
        }

        if (user == null) {
            sendResponse(exchange, 401, "{\"error\": \"Unauthorized\"}");
            return;
        }

        int userId = user.getId();

        InputStream requestBody = exchange.getRequestBody();
        MediaDTO mediaDTO = objectMapper.readValue(requestBody, MediaDTO.class);
        
        Media mediaInput = new Media.Builder()
                .title(mediaDTO.getTitle())
                .description(mediaDTO.getDescription())
                .mediaType(mediaDTO.getMediaType())
                .releaseYear(mediaDTO.getReleaseYear())
                .ageRestriction(mediaDTO.getAgeRestriction())
                .genres(mediaDTO.getGenres())
                .build();

        Media createdMedia = mediaService.createMedia(mediaInput, userId);
        String response = objectMapper.writeValueAsString(createdMedia);
        sendResponse(exchange, 201, response);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) {
            return result;
        }
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }
}
