package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.dto.LeaderboardEntryDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LeaderboardHandler implements HttpHandler {
    private final RatingService ratingService;
    private final ObjectMapper objectMapper;

    public LeaderboardHandler(RatingService ratingService, ObjectMapper objectMapper) {
        this.ratingService = ratingService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("/api/leaderboard".equals(path) && "GET".equalsIgnoreCase(method)) {
            handleGetLeaderboard(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Not Found\"}");
        }
    }

    private void handleGetLeaderboard(HttpExchange exchange) throws IOException {
        try {
            List<LeaderboardEntryDTO> leaderboard = ratingService.getLeaderboard();
            String response = objectMapper.writeValueAsString(leaderboard);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"Internal Server Error\"}");
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
