package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Rating;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class GetUserRatingsHandler extends BaseHandler {
    private final RatingService ratingService;

    public GetUserRatingsHandler(UserService userService, RatingService ratingService, ObjectMapper objectMapper) {
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
            // /api/users/{id}/ratings
            int userId = parseIdFromPath(exchange.getRequestURI().getPath(), 3);

            List<Rating> ratings = ratingService.getUserRatings(userId);
            String response = objectMapper.writeValueAsString(ratings);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, e.getMessage());
        }
    }
}
