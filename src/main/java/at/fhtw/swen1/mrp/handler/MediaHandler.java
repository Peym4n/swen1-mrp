package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.UserService;
import at.fhtw.swen1.mrp.dto.MediaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MediaHandler implements HttpHandler {
    private final MediaService mediaService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public MediaHandler(MediaService mediaService, UserService userService) {
        this.mediaService = mediaService;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
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
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Not Found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"Internal Server Error: " + e.getMessage() + "\"}");
        }
    }

    private void handleGetMedia(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);

        String title = params.get("title");
        String mediaType = params.get("mediaType");
        Integer releaseYear = params.containsKey("releaseYear") ? Integer.parseInt(params.get("releaseYear")) : null;
        Integer ageRestriction = params.containsKey("ageRestriction") ? Integer.parseInt(params.get("ageRestriction")) : null;

        List<Media> mediaList = mediaService.getMedia(title, mediaType, releaseYear, ageRestriction);
        
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
