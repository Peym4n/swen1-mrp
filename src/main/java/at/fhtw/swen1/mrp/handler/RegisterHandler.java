package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.UserDTO;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class RegisterHandler extends BaseHandler {

    public RegisterHandler(UserService userService, ObjectMapper objectMapper) {
        super(objectMapper, userService);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            UserDTO dto = readBody(exchange, UserDTO.class);
            User user = new User.Builder()
                    .username(dto.getUsername())
                    .password(dto.getPassword())
                    .email(dto.getEmail())
                    .favoriteGenre(dto.getFavoriteGenre())
                    .build();

            User createdUser = userService.register(user);
            sendResponse(exchange, 201, "{\"message\": \"User registered\", \"id\": " + createdUser.getId() + "}");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 400, e.getMessage());
        }
    }
}
