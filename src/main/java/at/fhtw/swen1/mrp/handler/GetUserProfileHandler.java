package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.UserProfileDTO;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handler for retrieving a user's profile.
 */
public final class GetUserProfileHandler extends BaseHandler {
    /** Index of the user ID in the path. */
    private static final int USER_ID_PATH_INDEX = 3;

    /**
     * Constructor.
     *
     * @param userService the user service
     * @param objectMapper the object mapper
     */
    public GetUserProfileHandler(final UserService userService,
                                 final ObjectMapper objectMapper) {
        super(objectMapper, userService);
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, HttpURLConnection.HTTP_BAD_METHOD, "Method Not Allowed");
            return;
        }

        try {
            // Path: /api/users/{id}/profile
            int userId = parseIdFromPath(exchange.getRequestURI().getPath(), USER_ID_PATH_INDEX);

            UserProfileDTO profile = getUserService().getUserProfile(userId);
            String response = getObjectMapper().writeValueAsString(profile);
            sendResponse(exchange, HttpURLConnection.HTTP_OK, response);
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
