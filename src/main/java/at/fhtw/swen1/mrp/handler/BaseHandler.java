package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseHandler implements HttpHandler {
    protected final ObjectMapper objectMapper;
    protected final UserService userService; // Common dependency for auth

    public BaseHandler(ObjectMapper objectMapper, UserService userService) {
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    protected void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
    
    protected void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String jsonError = String.format("{\"error\": \"%s\"}", message.replace("\"", "\\\""));
        sendResponse(exchange, statusCode, jsonError);
    }

    protected User authenticate(HttpExchange exchange) throws IOException {
        if (userService == null) {
            throw new IllegalStateException("UserService not initialized in handler");
        }
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
    
    protected <T> T readBody(HttpExchange exchange, Class<T> clazz) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        return objectMapper.readValue(requestBody, clazz);
    }
    
    protected int parseIdFromPath(String path, int index) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[index]);
    }
    
    protected Map<String, String> parseQuery(String query) {
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
