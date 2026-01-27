package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.LeaderboardEntryDTO;
import at.fhtw.swen1.mrp.service.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Handler for the leaderboard.
 */
public final class LeaderboardHandler implements HttpHandler {
    /** Service for rating operations. */
    private final RatingService ratingService;
    /** Object mapper for JSON serialization. */
    private final ObjectMapper objectMapper;

    /**
     * Constructor.
     *
     * @param ratingServiceArg the rating service
     * @param objectMapperArg the object mapper
     */
    public LeaderboardHandler(final RatingService ratingServiceArg,
                              final ObjectMapper objectMapperArg) {
        this.ratingService = ratingServiceArg;
        this.objectMapper = objectMapperArg;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("/api/leaderboard".equals(path) && "GET".equalsIgnoreCase(method)) {
            handleGetLeaderboard(exchange);
        } else {
            sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\": \"Not Found\"}");
        }
    }

    private void handleGetLeaderboard(final HttpExchange exchange) throws IOException {
        try {
            List<LeaderboardEntryDTO> leaderboard = ratingService.getLeaderboard();
            String response = objectMapper.writeValueAsString(leaderboard);
            sendResponse(exchange, HttpURLConnection.HTTP_OK, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\": \"Internal Server Error\"}");
        }
    }

    private void sendResponse(final HttpExchange exchange, final int statusCode,
                              final String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}
