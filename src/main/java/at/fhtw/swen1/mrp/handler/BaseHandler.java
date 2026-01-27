package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Base handler providing common functionality for HTTP handlers.
 */
public abstract class BaseHandler implements HttpHandler {
    /** JSON Object Mapper. */
    private final ObjectMapper objectMapper;
    /** User Service for authentication. */
    private final UserService userService;

    /**
     * Constructor.
     *
     * @param objectMapperArg the object mapper
     * @param userServiceArg the user service
     */
    public BaseHandler(final ObjectMapper objectMapperArg,
                       final UserService userServiceArg) {
        this.objectMapper = objectMapperArg;
        this.userService = userServiceArg;
    }

    /**
     * Gets the object mapper.
     *
     * @return the object mapper
     */
    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Gets the user service.
     *
     * @return the user service
     */
    protected UserService getUserService() {
        return userService;
    }

    /**
     * Sends an HTTP response.
     *
     * @param exchange the HTTP exchange
     * @param statusCode the HTTP status code
     * @param response the response body
     * @throws IOException if an I/O error occurs
     */
    protected void sendResponse(final HttpExchange exchange, final int statusCode, final String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    /**
     * Sends an error response.
     *
     * @param exchange the HTTP exchange
     * @param statusCode the HTTP status code
     * @param message the error message
     * @throws IOException if an I/O error occurs
     */
    protected void sendError(final HttpExchange exchange, final int statusCode, final String message) throws IOException {
        String jsonError = String.format("{\"error\": \"%s\"}", message.replace("\"", "\\\""));
        sendResponse(exchange, statusCode, jsonError);
    }

    /**
     * Authenticates the user based on the Authorization header.
     *
     * @param exchange the HTTP exchange
     * @return the authenticated User, or null if authentication fails
     * @throws IOException if an I/O error occurs during response sending
     */
    protected User authenticate(final HttpExchange exchange) throws IOException {
        if (userService == null) {
            throw new IllegalStateException("UserService not initialized in handler");
        }
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        User user = null;
        if (authHeader != null) {
            user = userService.getUserByToken(authHeader).orElse(null);
        }
        if (user == null) {
            sendResponse(exchange, HttpURLConnection.HTTP_UNAUTHORIZED, "{\"error\": \"Unauthorized\"}");
            return null;
        }
        return user;
    }

    /**
     * Reads and parses the request body.
     *
     * @param exchange the HTTP exchange
     * @param clazz the class to parse into
     * @param <T> the type of the object
     * @return the parsed object
     * @throws IOException if an I/O/Parsing error occurs
     */
    protected <T> T readBody(final HttpExchange exchange, final Class<T> clazz) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        return objectMapper.readValue(requestBody, clazz);
    }

    /**
     * Parses an ID from the path.
     *
     * @param path the request path
     * @param index the index of the path segment containing the ID
     * @return the parsed ID
     */
    protected int parseIdFromPath(final String path, final int index) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[index]);
    }

    /**
     * Parses the query string into a map.
     *
     * @param query the query string
     * @return a map of query parameters
     */
    protected Map<String, String> parseQuery(final String query) {
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
