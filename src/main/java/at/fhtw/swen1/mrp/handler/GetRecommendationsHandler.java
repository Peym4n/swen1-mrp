package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class GetRecommendationsHandler extends BaseHandler {

    public GetRecommendationsHandler(UserService userService, ObjectMapper objectMapper) {
        super(objectMapper, userService);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            User user = authenticate(exchange);
            if (user == null) return;

            // /api/users/{id}/recommendations
            int pathUserId = parseIdFromPath(exchange.getRequestURI().getPath(), 3);

            if (user.getId() != pathUserId) {
                sendError(exchange, 403, "Forbidden");
                return;
            }

            List<Media> recommendations = userService.getRecommendations(pathUserId);
            String response = objectMapper.writeValueAsString(recommendations);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, e.getMessage());
        }
    }
}
