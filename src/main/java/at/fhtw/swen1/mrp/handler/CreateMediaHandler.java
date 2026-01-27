package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.MediaDTO;
import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handler for creating new media.
 */
public final class CreateMediaHandler extends BaseHandler {
    /** Service for media operations. */
    private final MediaService mediaService;

    /**
     * Constructor.
     *
     * @param userServiceArg the user service
     * @param mediaServiceArg the media service
     * @param objectMapperArg the object mapper
     */
    public CreateMediaHandler(final UserService userServiceArg,
                              final MediaService mediaServiceArg,
                              final ObjectMapper objectMapperArg) {
        super(objectMapperArg, userServiceArg);
        this.mediaService = mediaServiceArg;
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

            MediaDTO mediaDTO = readBody(exchange, MediaDTO.class);
            Media mediaInput = new Media.Builder()
                    .title(mediaDTO.getTitle())
                    .description(mediaDTO.getDescription())
                    .mediaType(mediaDTO.getMediaType())
                    .releaseYear(mediaDTO.getReleaseYear())
                    .ageRestriction(mediaDTO.getAgeRestriction())
                    .genres(mediaDTO.getGenres())
                    .build();

            Media createdMedia = mediaService.createMedia(mediaInput, user.getId());
            String response = getObjectMapper().writeValueAsString(createdMedia);
            sendResponse(exchange, HttpURLConnection.HTTP_CREATED, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                    e.getMessage());
        }
    }
}
