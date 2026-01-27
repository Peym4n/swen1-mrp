package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.RatingDTO;
import at.fhtw.swen1.mrp.model.Rating;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handler for updating ratings.
 */
public final class UpdateRatingHandler extends BaseHandler {
    /** Service for rating operations. */
    private final RatingService ratingService;

    /** Index of the rating ID in the path. */
    private static final int RATING_ID_PATH_INDEX = 3;

    /**
     * Constructor.
     *
     * @param userServiceArg the user service
     * @param ratingServiceArg the rating service
     * @param objectMapperArg the object mapper
     */
    public UpdateRatingHandler(final UserService userServiceArg,
                               final RatingService ratingServiceArg,
                               final ObjectMapper objectMapperArg) {
        super(objectMapperArg, userServiceArg);
        this.ratingService = ratingServiceArg;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (!"PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, HttpURLConnection.HTTP_BAD_METHOD, "Method Not Allowed");
            return;
        }

        try {
            User user = authenticate(exchange);
            if (user == null) {
                return;
            }

            // /api/ratings/{id}
            int ratingId = parseIdFromPath(exchange.getRequestURI().getPath(), RATING_ID_PATH_INDEX);
            RatingDTO dto = readBody(exchange, RatingDTO.class);

            Rating updated = ratingService.updateRating(user.getId(), ratingId, dto.getStars(), dto.getComment());
            String response = getObjectMapper().writeValueAsString(updated);
            sendResponse(exchange, HttpURLConnection.HTTP_OK, response);
        } catch (IllegalArgumentException e) {
            sendError(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                    e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    e.getMessage());
        }
    }
}
