package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.UserService;
import at.fhtw.swen1.mrp.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class UserHandler implements HttpHandler {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserHandler(UserService userService) {
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
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
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Not Found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
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
