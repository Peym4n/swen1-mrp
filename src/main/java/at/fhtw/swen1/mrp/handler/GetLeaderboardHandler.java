package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.LeaderboardEntryDTO;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Handler for retrieving the leaderboard.
 */
public final class GetLeaderboardHandler extends BaseHandler {
    /** Service for rating operations. */
    private final RatingService ratingService;

    /**
     * Constructor.
     *
     * @param userServiceArg the user service
     * @param ratingServiceArg the rating service
     * @param objectMapperArg the object mapper
     */
    public GetLeaderboardHandler(final UserService userServiceArg,
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
            // Public endpoint, no authentication required
            List<LeaderboardEntryDTO> leaderboard = ratingService.getLeaderboard();
            String response = getObjectMapper().writeValueAsString(leaderboard);
            sendResponse(exchange, HttpURLConnection.HTTP_OK, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    "Internal Server Error");
        }
    }
}
