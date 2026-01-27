package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Optional;

/**
 * Handler for retrieving media by ID.
 */
public final class GetMediaByIdHandler extends BaseHandler {
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
    public GetMediaByIdHandler(final UserService userServiceArg,
                               final MediaService mediaServiceArg,
                               final ObjectMapper objectMapperArg) {
        super(objectMapperArg, userServiceArg);
        this.mediaService = mediaServiceArg;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, HttpURLConnection.HTTP_BAD_METHOD, "Method Not Allowed");
            return;
        }

        try {
            // /api/media/{id}
            int mediaId = parseIdFromPath(exchange.getRequestURI().getPath(), MEDIA_ID_PATH_INDEX);

            Optional<Media> media = mediaService.getMediaById(mediaId);
            if (media.isPresent()) {
                String response = getObjectMapper().writeValueAsString(media.get());
                sendResponse(exchange, HttpURLConnection.HTTP_OK, response);
            } else {
                sendError(exchange, HttpURLConnection.HTTP_NOT_FOUND, "Media not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                    e.getMessage());
        }
    }
}
