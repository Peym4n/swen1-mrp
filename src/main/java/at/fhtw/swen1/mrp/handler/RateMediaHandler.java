package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.RatingDTO;
import at.fhtw.swen1.mrp.model.Rating;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class RateMediaHandler extends BaseHandler {
    private final RatingService ratingService;

    public RateMediaHandler(UserService userService, RatingService ratingService, ObjectMapper objectMapper) {
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
            User user = authenticate(exchange);
            if (user == null) return;

            // /api/media/{id}/rate
            int mediaId = parseIdFromPath(exchange.getRequestURI().getPath(), 3);

            RatingDTO ratingDTO = readBody(exchange, RatingDTO.class);
            Rating rating = ratingService.rateMedia(user.getId(), mediaId, ratingDTO.getStars(), ratingDTO.getComment());
            
            String response = objectMapper.writeValueAsString(rating);
            sendResponse(exchange, 201, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, e.getMessage());
        }
    }
}
