package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ConfirmRatingHandler extends BaseHandler {
    private final RatingService ratingService;

    public ConfirmRatingHandler(UserService userService, RatingService ratingService, ObjectMapper objectMapper) {
        super(objectMapper, userService);
        this.ratingService = ratingService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            // Authentication required
            User user = authenticate(exchange);
            if (user == null) return;
            int userId = user.getId();

            // /api/ratings/{id}/confirm
            int ratingId = parseIdFromPath(exchange.getRequestURI().getPath(), 3);

            ratingService.confirmRating(ratingId, userId);
            sendResponse(exchange, 200, "{\"message\": \"Rating confirmed\"}");
        } catch (IllegalArgumentException e) {
            sendError(exchange, 404, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, e.getMessage());
        }
    }
}
