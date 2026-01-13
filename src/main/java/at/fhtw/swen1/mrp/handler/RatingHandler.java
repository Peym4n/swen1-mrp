package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Rating;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import at.fhtw.swen1.mrp.dto.RatingDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RatingHandler implements HttpHandler {
    private final RatingService ratingService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public RatingHandler(RatingService ratingService, UserService userService) {
        this.ratingService = ratingService;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            // Check Auth for all rating operations
            User user = authenticate(exchange);
            if (user == null) {
                return; // Response already sent
            }

            // Path parsing: /api/ratings/{id}/...
            String prefix = "/api/ratings/";
            if (path.startsWith(prefix)) {
                String subPath = path.substring(prefix.length());
                String[] parts = subPath.split("/");

                if (parts.length == 1) {
                    // /api/ratings/{id}
                    try {
                        int ratingId = Integer.parseInt(parts[0]);
                        if ("PUT".equalsIgnoreCase(method)) {
                            handleUpdateRating(exchange, ratingId, user);
                        } else if ("DELETE".equalsIgnoreCase(method)) {
                            handleDeleteRating(exchange, ratingId, user);
                        } else {
                            sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
                        }
                    } catch (NumberFormatException e) {
                        sendResponse(exchange, 400, "{\"error\": \"Invalid ID format\"}");
                    }
                } else if (parts.length == 2) {
                    // /api/ratings/{id}/confirm or /like
                    try {
                        int ratingId = Integer.parseInt(parts[0]);
                        String action = parts[1];

                        if ("POST".equalsIgnoreCase(method)) {
                            if ("confirm".equals(action)) {
                                handleConfirmRating(exchange, ratingId);
                            } else if ("like".equals(action)) {
                                handleLikeRating(exchange, ratingId, user);
                            } else {
                                sendResponse(exchange, 404, "{\"error\": \"Action not found\"}");
                            }
                        } else {
                            sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
                        }

                    } catch (NumberFormatException e) {
                        sendResponse(exchange, 400, "{\"error\": \"Invalid ID format\"}");
                    }
                } else {
                    sendResponse(exchange, 404, "{\"error\": \"Not Found\"}");
                }
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Not Found\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"Internal Server Error: " + e.getMessage() + "\"}");
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

    private void handleUpdateRating(HttpExchange exchange, int ratingId, User user) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        RatingDTO ratingDTO = objectMapper.readValue(requestBody, RatingDTO.class);

        try {
            Rating updated = ratingService.updateRating(user.getId(), ratingId, ratingDTO.getStars(), ratingDTO.getComment());
            String response = objectMapper.writeValueAsString(updated);
            sendResponse(exchange, 200, response);
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleDeleteRating(HttpExchange exchange, int ratingId, User user) throws IOException {
        try {
            ratingService.deleteRating(user.getId(), ratingId);
            sendResponse(exchange, 204, "");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleConfirmRating(HttpExchange exchange, int ratingId) throws IOException {
        try {
            ratingService.confirmRating(ratingId);
            sendResponse(exchange, 200, "{\"message\": \"Rating confirmed\"}");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 404, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleLikeRating(HttpExchange exchange, int ratingId, User user) throws IOException {
        try {
            ratingService.likeRating(user.getId(), ratingId);
            sendResponse(exchange, 200, "{\"message\": \"Rating liked\"}");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 404, "{\"error\": \"" + e.getMessage() + "\"}");
        }
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
