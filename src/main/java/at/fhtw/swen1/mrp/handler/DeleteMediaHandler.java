package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handler for deleting media.
 */
public final class DeleteMediaHandler extends BaseHandler {
    /** Service for media operations. */
    private final MediaService mediaService;

    /** Index of the media ID in the path. */
    private static final int MEDIA_ID_PATH_INDEX = 3;

    /**
     * Constructor.
     *
     * @param userServiceArg the user service
     * @param mediaServiceArg the media service
     * @param objectMapperArg the object mapper
     */
    public DeleteMediaHandler(final UserService userServiceArg,
                              final MediaService mediaServiceArg,
                              final ObjectMapper objectMapperArg) {
        super(objectMapperArg, userServiceArg);
        this.mediaService = mediaServiceArg;
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

            int mediaId = parseIdFromPath(exchange.getRequestURI().getPath(), MEDIA_ID_PATH_INDEX);

            mediaService.deleteMedia(mediaId, user.getId());
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
