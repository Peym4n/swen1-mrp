package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.UserDTO;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handler for updating a user's profile.
 */
public final class UpdateUserProfileHandler extends BaseHandler {

    /** Index of the user ID in the path. */
    private static final int USER_ID_PATH_INDEX = 3;

    /**
     * Constructor.
     *
     * @param userServiceArg the user service
     * @param objectMapperArg the object mapper
     */
    public UpdateUserProfileHandler(final UserService userServiceArg,
                                    final ObjectMapper objectMapperArg) {
        super(objectMapperArg, userServiceArg);
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (!"PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, HttpURLConnection.HTTP_BAD_METHOD,
                    "Method Not Allowed");
            return;
        }

        try {
            User user = authenticate(exchange);
            if (user == null) {
                return;
            }

            // Path: /api/users/{id}/profile
            int pathUserId = parseIdFromPath(exchange.getRequestURI().getPath(),
                    USER_ID_PATH_INDEX);

            if (user.getId() != pathUserId) {
                sendError(exchange, HttpURLConnection.HTTP_FORBIDDEN,
                        "Forbidden");
                return;
            }

            UserDTO dto = readBody(exchange, UserDTO.class);
            getUserService().updateProfile(user.getId(), dto.getEmail(),
                    dto.getFavoriteGenre());

            sendResponse(exchange, HttpURLConnection.HTTP_OK,
                    "{\"message\": \"Profile updated\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                    e.getMessage());
        }
    }
}
