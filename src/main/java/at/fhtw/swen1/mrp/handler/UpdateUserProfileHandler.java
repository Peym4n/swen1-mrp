package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.UserDTO;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class UpdateUserProfileHandler extends BaseHandler {

    public UpdateUserProfileHandler(UserService userService, ObjectMapper objectMapper) {
        super(objectMapper, userService);
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

            // Path: /api/users/{id}/profile
            int pathUserId = parseIdFromPath(exchange.getRequestURI().getPath(), 3);

            if (user.getId() != pathUserId) {
                sendError(exchange, 403, "Forbidden");
                return;
            }

            UserDTO dto = readBody(exchange, UserDTO.class);
            userService.updateProfile(user.getId(), dto.getEmail(), dto.getFavoriteGenre());

            sendResponse(exchange, 200, "{\"message\": \"Profile updated\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 400, e.getMessage());
        }
    }
}
