package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Rating;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Handler for retrieving a user's ratings.
 */
public final class GetUserRatingsHandler extends BaseHandler {
    /** Service for rating operations. */
    private final RatingService ratingService;

    /** Index of the user ID in the path. */
    private static final int USER_ID_PATH_INDEX = 3;

    /**
     * Constructor.
     *
     * @param userServiceArg the user service
     * @param ratingServiceArg the rating service
     * @param objectMapperArg the object mapper
     */
    public GetUserRatingsHandler(final UserService userServiceArg,
                                 final RatingService ratingServiceArg,
                                 final ObjectMapper objectMapperArg) {
        super(objectMapperArg, userServiceArg);
        this.ratingService = ratingServiceArg;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, HttpURLConnection.HTTP_BAD_METHOD, "Method Not Allowed");
            return;
        }

        try {
            // /api/users/{id}/ratings
            int userId = parseIdFromPath(exchange.getRequestURI().getPath(), USER_ID_PATH_INDEX);

            List<Rating> ratings = ratingService.getUserRatings(userId);
            String response = getObjectMapper().writeValueAsString(ratings);
            sendResponse(exchange, HttpURLConnection.HTTP_OK, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    e.getMessage());
        }
    }
}
