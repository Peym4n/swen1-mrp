package at.fhtw.swen1.mrp.handler;

import at.fhtw.swen1.mrp.dto.UserDTO;
import at.fhtw.swen1.mrp.model.User;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handler for user registration.
 */
public final class RegisterHandler extends BaseHandler {

    /**
     * Constructor.
     *
     * @param userServiceArg the user service
     * @param objectMapperArg the object mapper
     */
    public RegisterHandler(final UserService userServiceArg,
                           final ObjectMapper objectMapperArg) {
        super(objectMapperArg, userServiceArg);
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, HttpURLConnection.HTTP_BAD_METHOD, "Method Not Allowed");
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

            User createdUser = getUserService().register(user);
            sendResponse(exchange, HttpURLConnection.HTTP_CREATED,
                    "{\"message\": \"User registered\", \"id\": " + createdUser.getId() + "}");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                    e.getMessage());
        }
    }
}
