package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.UserProfileDTO;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class GetUserProfileHandler extends BaseHandler {

    public GetUserProfileHandler(UserService userService, ObjectMapper objectMapper) {
        super(objectMapper, userService);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            // Path: /api/users/{id}/profile
            int userId = parseIdFromPath(exchange.getRequestURI().getPath(), 3);
            
            UserProfileDTO profile = userService.getUserProfile(userId);
            String response = objectMapper.writeValueAsString(profile);
            sendResponse(exchange, 200, response);
        } catch (IllegalArgumentException e) {
            sendError(exchange, 404, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, e.getMessage());
        }
    }
}
