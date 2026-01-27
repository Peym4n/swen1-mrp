package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * Handler for retrieving media with filtering.
 */
public final class GetMediaHandler extends BaseHandler {
    /** Service for media operations. */
    private final MediaService mediaService;

    /**
     * Constructor.
     *
     * @param userServiceArg the user service
     * @param mediaServiceArg the media service
     * @param objectMapperArg the object mapper
     */
    public GetMediaHandler(final UserService userServiceArg,
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
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQuery(query);

            String title = params.get("title");
            String mediaType = params.get("mediaType");
            String genre = params.get("genre");
            Integer releaseYear = params.containsKey("releaseYear") ? Integer.parseInt(params.get("releaseYear")) : null;
            Integer ageRestriction = params.containsKey("ageRestriction") ? Integer.parseInt(params.get("ageRestriction")) : null;
            Double minRating = params.containsKey("rating") ? Double.parseDouble(params.get("rating")) : null;

            List<Media> mediaList = mediaService.getMedia(title, mediaType, releaseYear, ageRestriction, genre, minRating);
            String response = getObjectMapper().writeValueAsString(mediaList);
            sendResponse(exchange, HttpURLConnection.HTTP_OK, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    e.getMessage());
        }
    }
}
