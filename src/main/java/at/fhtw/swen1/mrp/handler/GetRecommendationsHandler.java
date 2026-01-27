package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Handler for retrieving media recommendations for a user.
 */
public final class GetRecommendationsHandler extends BaseHandler {

    /** Index of the user ID in the path. */
    private static final int USER_ID_PATH_INDEX = 3;

    /**
     * Constructor.
     *
     * @param userServiceArg the user service
     * @param objectMapperArg the object mapper
     */
    public GetRecommendationsHandler(final UserService userServiceArg,
                                     final ObjectMapper objectMapperArg) {
        super(objectMapperArg, userServiceArg);
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, HttpURLConnection.HTTP_BAD_METHOD, "Method Not Allowed");
            return;
        }

        try {
            User user = authenticate(exchange);
            if (user == null) {
                return;
            }

            // /api/users/{id}/recommendations
            int pathUserId = parseIdFromPath(exchange.getRequestURI().getPath(), USER_ID_PATH_INDEX);

            if (user.getId() != pathUserId) {
                sendError(exchange, HttpURLConnection.HTTP_FORBIDDEN, "Forbidden");
                return;
            }

            List<Media> recommendations = getUserService().getRecommendations(pathUserId);
            String response = getObjectMapper()
                    .writeValueAsString(recommendations);
            sendResponse(exchange, HttpURLConnection.HTTP_OK, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    e.getMessage());
        }
    }
}
