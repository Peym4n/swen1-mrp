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
 * Handler for rating media.
 */
public final class RateMediaHandler extends BaseHandler {
    /** Service for rating operations. */
    private final RatingService ratingService;

    /** Index of the media ID in the path. */
    private static final int MEDIA_ID_PATH_INDEX = 3;

    /**
     * Constructor.
     *
     * @param userServiceArg the user service
     * @param ratingServiceArg the rating service
     * @param objectMapperArg the object mapper
     */
    public RateMediaHandler(final UserService userServiceArg,
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
            User user = authenticate(exchange);
            if (user == null) {
                return;
            }

            // /api/media/{id}/rate
            int mediaId = parseIdFromPath(exchange.getRequestURI().getPath(), MEDIA_ID_PATH_INDEX);

            RatingDTO ratingDTO = readBody(exchange, RatingDTO.class);
            Rating rating = ratingService.rateMedia(user.getId(), mediaId, ratingDTO.getStars(), ratingDTO.getComment());

            String response = getObjectMapper().writeValueAsString(rating);
            sendResponse(exchange, HttpURLConnection.HTTP_CREATED, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendError(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                    e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    e.getMessage());
        }
    }
}
