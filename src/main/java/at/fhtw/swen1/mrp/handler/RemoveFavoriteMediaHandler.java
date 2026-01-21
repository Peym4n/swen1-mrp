package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.FavoriteService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class RemoveFavoriteMediaHandler extends BaseHandler {
    private final FavoriteService favoriteService;

    public RemoveFavoriteMediaHandler(UserService userService, FavoriteService favoriteService, ObjectMapper objectMapper) {
        super(objectMapper, userService);
        this.favoriteService = favoriteService;
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

            // /api/media/{id}/favorite
            int mediaId = parseIdFromPath(exchange.getRequestURI().getPath(), 3);
            
            favoriteService.removeFavorite(user.getId(), mediaId);
            sendResponse(exchange, 200, "{\"message\": \"Removed from favorites\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, e.getMessage());
        }
    }
}
