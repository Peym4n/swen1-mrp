package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.FavoriteService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class AddFavoriteMediaHandler extends BaseHandler {
    private final FavoriteService favoriteService;

    public AddFavoriteMediaHandler(UserService userService, FavoriteService favoriteService, ObjectMapper objectMapper) {
        super(objectMapper, userService);
        this.favoriteService = favoriteService;
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

            // /api/media/{id}/favorite
            int mediaId = parseIdFromPath(exchange.getRequestURI().getPath(), 3);
            
            favoriteService.addFavorite(user.getId(), mediaId);
            sendResponse(exchange, 201, "{\"message\": \"Added to favorites\"}");
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage()); // Likely media not found or already favorite
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, e.getMessage());
        }
    }
}
