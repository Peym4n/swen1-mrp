package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.MediaDTO;
import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class CreateMediaHandler extends BaseHandler {
    private final MediaService mediaService;

    public CreateMediaHandler(UserService userService, MediaService mediaService, ObjectMapper objectMapper) {
        super(objectMapper, userService);
        this.mediaService = mediaService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            User user = authenticate(exchange);
            if (user == null) return;

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
            String response = objectMapper.writeValueAsString(createdMedia);
            sendResponse(exchange, 201, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 400, e.getMessage());
        }
    }
}
