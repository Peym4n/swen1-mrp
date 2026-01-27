package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.FavoriteService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handler for adding media to favorites.
 */
public final class AddFavoriteMediaHandler extends BaseHandler {
    /** Service for favorite operations. */
    private final FavoriteService favoriteService;

    /** Index of the media ID in the path. */
    private static final int MEDIA_ID_PATH_INDEX = 3;

    /**
     * Constructor.
     *
     * @param userServiceArg the user service
     * @param favoriteServiceArg the favorite service
     * @param objectMapperArg the object mapper
     */
    public AddFavoriteMediaHandler(final UserService userServiceArg,
                                   final FavoriteService favoriteServiceArg,
                                   final ObjectMapper objectMapperArg) {
        super(objectMapperArg, userServiceArg);
        this.favoriteService = favoriteServiceArg;
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

            // /api/media/{id}/favorite
            int mediaId = parseIdFromPath(exchange.getRequestURI().getPath(), MEDIA_ID_PATH_INDEX);

            favoriteService.addFavorite(user.getId(), mediaId);
            sendResponse(exchange, HttpURLConnection.HTTP_CREATED, "{\"message\": \"Added to favorites\"}");
        } catch (IllegalArgumentException e) {
            // Likely media not found or already favorite
            sendError(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                    e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    e.getMessage());
        }
    }
}
