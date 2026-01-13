package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Rating;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import at.fhtw.swen1.mrp.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserHandler implements HttpHandler {
    private final UserService userService;
    private final RatingService ratingService;
    private final ObjectMapper objectMapper;

    public UserHandler(UserService userService, RatingService ratingService) {
        this.userService = userService;
        this.ratingService = ratingService;
        this.objectMapper = new ObjectMapper();
    }
    
    public UserHandler(UserService userService) {
        this(userService, null);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("/api/users/register".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleRegister(exchange);
            } else if ("/api/users/login".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleLogin(exchange);
            } else if (path.matches("^/api/users/\\d+/ratings$") && "GET".equalsIgnoreCase(method)) {
                handleGetUserRatings(exchange, path);
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Not Found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleGetUserRatings(HttpExchange exchange, String path) throws IOException {
        if (ratingService == null) {
            sendResponse(exchange, 500, "{\"error\": \"RatingService not initialized in handler\"}");
            return;
        }

        // /api/users/{id}/ratings
        String[] parts = path.split("/");
        int userId = Integer.parseInt(parts[3]);

        List<Rating> ratings = ratingService.getUserRatings(userId);
        String response = objectMapper.writeValueAsString(ratings);
        sendResponse(exchange, 200, response);
    }


    private void handleRegister(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        UserDTO dto = objectMapper.readValue(requestBody, UserDTO.class);
        User user = new User.Builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .favoriteGenre(dto.getFavoriteGenre())
                .build();

        userService.register(user);
        sendResponse(exchange, 201, "{\"message\": \"User registered\"}");
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        UserDTO dto = objectMapper.readValue(requestBody, UserDTO.class);
        
        String token = userService.login(dto.getUsername(), dto.getPassword());
        sendResponse(exchange, 200, "{\"token\": \"" + token + "\"}");
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
