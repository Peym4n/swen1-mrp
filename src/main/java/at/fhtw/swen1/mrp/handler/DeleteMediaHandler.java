package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class DeleteMediaHandler extends BaseHandler {
    private final MediaService mediaService;

    public DeleteMediaHandler(UserService userService, MediaService mediaService, ObjectMapper objectMapper) {
        super(objectMapper, userService);
        this.mediaService = mediaService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            User user = authenticate(exchange);
            if (user == null) return;

            int mediaId = parseIdFromPath(exchange.getRequestURI().getPath(), 3);

            mediaService.deleteMedia(mediaId, user.getId());
            sendResponse(exchange, 204, "");
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, e.getMessage());
        }
    }
}
