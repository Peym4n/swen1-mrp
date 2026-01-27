package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.UserDTO;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handler for user login.
 */
public final class LoginHandler extends BaseHandler {

    /**
     * Constructor.
     *
     * @param userService the user service
     * @param objectMapper the object mapper
     */
    public LoginHandler(final UserService userService,
                        final ObjectMapper objectMapper) {
        super(objectMapper, userService);
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, HttpURLConnection.HTTP_BAD_METHOD, "Method Not Allowed");
            return;
        }

        try {
            UserDTO dto = readBody(exchange, UserDTO.class);
            String token = getUserService().login(dto.getUsername(), dto.getPassword());
            sendResponse(exchange, HttpURLConnection.HTTP_OK, "{\"token\": \"" + token + "\"}");
        } catch (Exception e) {
            // UserService throws RuntimeException for invalid creds
            sendError(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                    e.getMessage());
        }
    }
}
