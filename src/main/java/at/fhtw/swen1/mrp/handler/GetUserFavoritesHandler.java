package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.service.FavoriteService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Handler for retrieving a user's favorite media.
 */
public final class GetUserFavoritesHandler extends BaseHandler {
    /** Service for favorite operations. */
    private final FavoriteService favoriteService;

    /** Index of the user ID in the path. */
    private static final int USER_ID_PATH_INDEX = 3;

    /**
     * Constructor.
     *
     * @param userServiceArg the user service
     * @param favoriteServiceArg the favorite service
     * @param objectMapperArg the object mapper
     */
    public GetUserFavoritesHandler(final UserService userServiceArg,
                                   final FavoriteService favoriteServiceArg,
                                   final ObjectMapper objectMapperArg) {
        super(objectMapperArg, userServiceArg);
        this.favoriteService = favoriteServiceArg;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, HttpURLConnection.HTTP_BAD_METHOD, "Method Not Allowed");
            return;
        }

        try {
            // /api/users/{id}/favorites
            int userId = parseIdFromPath(exchange.getRequestURI().getPath(), USER_ID_PATH_INDEX);

            List<Media> favorites = favoriteService.getFavorites(userId);
            String response = getObjectMapper().writeValueAsString(favorites);
            sendResponse(exchange, HttpURLConnection.HTTP_OK, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    e.getMessage());
        }
    }
}
