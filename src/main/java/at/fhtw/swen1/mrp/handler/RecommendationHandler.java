package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.RecommendationService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RecommendationHandler implements HttpHandler {
    private final RecommendationService recommendationService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public RecommendationHandler(RecommendationService recommendationService, UserService userService, ObjectMapper objectMapper) {
        this.recommendationService = recommendationService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // /api/users/{id}/recommendations
        if (path.matches("^/api/users/\\d+/recommendations$") && "GET".equalsIgnoreCase(method)) {
            handleGetRecommendations(exchange, path);
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Not Found\"}");
        }
    }

    private void handleGetRecommendations(HttpExchange exchange, String path) throws IOException {
        // Authenticate
        User user = authenticate(exchange);
        if (user == null) return;

        // Parse User ID from path
        String[] parts = path.split("/");
        int pathUserId = Integer.parseInt(parts[3]);

        // Authorization check
        if (user.getId() != pathUserId) {
             sendResponse(exchange, 403, "{\"error\": \"Forbidden\"}");
             return;
        }

        try {
            List<Media> recommendations = recommendationService.getRecommendations(pathUserId);
            String response = objectMapper.writeValueAsString(recommendations);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"Internal Server Error\"}");
        }
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
