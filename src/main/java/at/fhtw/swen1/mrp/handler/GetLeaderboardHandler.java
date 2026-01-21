package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.LeaderboardEntryDTO;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class GetLeaderboardHandler extends BaseHandler {
    private final RatingService ratingService;

    public GetLeaderboardHandler(UserService userService, RatingService ratingService, ObjectMapper objectMapper) {
        super(objectMapper, userService);
        this.ratingService = ratingService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            // Public endpoint, no authentication required
            List<LeaderboardEntryDTO> leaderboard = ratingService.getLeaderboard();
            String response = objectMapper.writeValueAsString(leaderboard);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal Server Error");
        }
    }
}
