package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GetMediaHandler extends BaseHandler {
    private final MediaService mediaService;

    public GetMediaHandler(UserService userService, MediaService mediaService, ObjectMapper objectMapper) {
        super(objectMapper, userService);
        this.mediaService = mediaService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
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
            String response = objectMapper.writeValueAsString(mediaList);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, e.getMessage());
        }
    }
}
