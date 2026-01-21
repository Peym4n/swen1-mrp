package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.Media;
import at.fhtw.swen1.mrp.service.FavoriteService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class GetUserFavoritesHandler extends BaseHandler {
    private final FavoriteService favoriteService;

    public GetUserFavoritesHandler(UserService userService, FavoriteService favoriteService, ObjectMapper objectMapper) {
        super(objectMapper, userService);
        this.favoriteService = favoriteService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            // /api/users/{id}/favorites
            int userId = parseIdFromPath(exchange.getRequestURI().getPath(), 3);

            List<Media> favorites = favoriteService.getFavorites(userId);
            String response = objectMapper.writeValueAsString(favorites);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, e.getMessage());
        }
    }
}
