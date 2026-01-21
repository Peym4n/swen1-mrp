package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Optional;

public class GetMediaByIdHandler extends BaseHandler {
    private final MediaService mediaService;

    public GetMediaByIdHandler(UserService userService, MediaService mediaService, ObjectMapper objectMapper) {
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
            // /api/media/{id}
            int mediaId = parseIdFromPath(exchange.getRequestURI().getPath(), 3);

            Optional<Media> media = mediaService.getMediaById(mediaId);
            if (media.isPresent()) {
                String response = objectMapper.writeValueAsString(media.get());
                sendResponse(exchange, 200, response);
            } else {
                sendError(exchange, 404, "Media not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 400, e.getMessage());
        }
    }
}
