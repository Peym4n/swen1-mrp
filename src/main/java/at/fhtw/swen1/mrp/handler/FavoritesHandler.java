package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.FavoriteService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FavoritesHandler implements HttpHandler {
    private final FavoriteService favoriteService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public FavoritesHandler(FavoriteService favoriteService, UserService userService) {
        this.favoriteService = favoriteService;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (path.matches("^/api/media/\\d+/favorite$")) {
                if ("POST".equalsIgnoreCase(method)) {
                    handleAddFavorite(exchange, path);
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    handleRemoveFavorite(exchange, path);
                } else {
                    sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
                }
            } else if (path.matches("^/api/users/\\d+/favorites$") && "GET".equalsIgnoreCase(method)) {
                handleGetFavorites(exchange, path);
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Not Found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleAddFavorite(HttpExchange exchange, String path) throws IOException {
        User user = authenticate(exchange);
        if (user == null) return;

        int mediaId = parseIdFromPath(path, 3); // /api/media/{id}/favorite -> index 3

        favoriteService.addFavorite(user.getId(), mediaId);
        sendResponse(exchange, 201, "{\"message\": \"Added to favorites\"}");
    }

    private void handleRemoveFavorite(HttpExchange exchange, String path) throws IOException {
        User user = authenticate(exchange);
        if (user == null) return;

        int mediaId = parseIdFromPath(path, 3);

        favoriteService.removeFavorite(user.getId(), mediaId);
        sendResponse(exchange, 200, "{\"message\": \"Removed from favorites\"}");
    }

    private void handleGetFavorites(HttpExchange exchange, String path) throws IOException {
        // Public access allowed? Or should we require login?
        // Let's assume public logic for 'social' features, but maybe check auth if strict?
        // Rules don't specify. I'll allow public access to see other users' favorites.
        
        int userId = parseIdFromPath(path, 3); // /api/users/{id}/favorites -> index 3

        List<Media> favorites = favoriteService.getFavorites(userId);
        String response = objectMapper.writeValueAsString(favorites);
        sendResponse(exchange, 200, response);
    }

    private int parseIdFromPath(String path, int index) {
        String[] parts = path.split("/");
        if (parts.length > index) {
            return Integer.parseInt(parts[index]);
        }
        throw new IllegalArgumentException("Invalid path for ID parsing");
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

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}
