package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handler for confirming a rating.
 */
public final class ConfirmRatingHandler extends BaseHandler {
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
    public ConfirmRatingHandler(final UserService userServiceArg,
                                final RatingService ratingServiceArg,
                                final ObjectMapper objectMapperArg) {
        super(objectMapperArg, userServiceArg);
        this.ratingService = ratingServiceArg;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, HttpURLConnection.HTTP_BAD_METHOD, "Method Not Allowed");
            return;
        }

        try {
            // Authentication required
            User user = authenticate(exchange);
            if (user == null) {
                return;
            }
            int userId = user.getId();

            // /api/ratings/{id}/confirm
            int ratingId = parseIdFromPath(exchange.getRequestURI().getPath(), RATING_ID_PATH_INDEX);

            ratingService.confirmRating(ratingId, userId);
            sendResponse(exchange, HttpURLConnection.HTTP_OK, "{\"message\": \"Rating confirmed\"}");
        } catch (IllegalArgumentException e) {
            sendError(exchange, HttpURLConnection.HTTP_NOT_FOUND,
                    e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    e.getMessage());
        }
    }
}
