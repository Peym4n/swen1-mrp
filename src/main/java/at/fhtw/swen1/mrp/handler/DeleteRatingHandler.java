package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handler for deleting ratings.
 */
public final class DeleteRatingHandler extends BaseHandler {
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
    public DeleteRatingHandler(final UserService userServiceArg,
                               final RatingService ratingServiceArg,
                               final ObjectMapper objectMapperArg) {
        super(objectMapperArg, userServiceArg);
        this.ratingService = ratingServiceArg;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (!"DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
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

            ratingService.deleteRating(user.getId(), ratingId);
            sendResponse(exchange, HttpURLConnection.HTTP_NO_CONTENT, "");
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
