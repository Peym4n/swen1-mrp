package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.MediaDTO;
import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class UpdateMediaHandler extends BaseHandler {
    private final MediaService mediaService;

    public UpdateMediaHandler(UserService userService, MediaService mediaService, ObjectMapper objectMapper) {
        super(objectMapper, userService);
        this.mediaService = mediaService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            User user = authenticate(exchange);
            if (user == null) return;

            int mediaId = parseIdFromPath(exchange.getRequestURI().getPath(), 3);
            MediaDTO mediaDTO = readBody(exchange, MediaDTO.class);

            Media mediaUpdates = new Media.Builder()
                    .title(mediaDTO.getTitle())
                    .description(mediaDTO.getDescription())
                    .mediaType(mediaDTO.getMediaType())
                    .releaseYear(mediaDTO.getReleaseYear())
                    .ageRestriction(mediaDTO.getAgeRestriction())
                    .genres(mediaDTO.getGenres())
                    .build();

            Media updated = mediaService.updateMedia(mediaId, mediaUpdates, user.getId());
            String response = objectMapper.writeValueAsString(updated);
            sendResponse(exchange, 200, response);
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, e.getMessage());
        }
    }
}
