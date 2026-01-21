package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.UserDTO;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class LoginHandler extends BaseHandler {

    public LoginHandler(UserService userService, ObjectMapper objectMapper) {
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
            String token = userService.login(dto.getUsername(), dto.getPassword());
            sendResponse(exchange, 200, "{\"token\": \"" + token + "\"}");
        } catch (Exception e) {
            // e.printStackTrace(); // Optional logging
            // UserService throws RuntimeException for invalid creds
            // We can check message or generic 400
             sendError(exchange, 400, e.getMessage()); // Or "Invalid credentials"
        }
    }
}
