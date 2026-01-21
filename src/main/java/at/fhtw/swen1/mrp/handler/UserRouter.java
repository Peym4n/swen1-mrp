package at.fhtw.swen1.mrp.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class UserRouter extends BaseRouter {
    // Defines the precise handlers for each operation
    private final LoginHandler loginHandler;
    private final RegisterHandler registerHandler;
    private final GetUserProfileHandler getUserProfileHandler;
    private final UpdateUserProfileHandler updateUserProfileHandler;
    private final GetUserRatingsHandler getUserRatingsHandler;
    private final GetUserFavoritesHandler getUserFavoritesHandler;
    private final GetRecommendationsHandler getRecommendationsHandler;

    public UserRouter(LoginHandler loginHandler, RegisterHandler registerHandler,
                       GetUserProfileHandler getUserProfileHandler, UpdateUserProfileHandler updateUserProfileHandler,
                       GetUserRatingsHandler getUserRatingsHandler, GetUserFavoritesHandler getUserFavoritesHandler,
                       GetRecommendationsHandler getRecommendationsHandler) {
        this.loginHandler = loginHandler;
        this.registerHandler = registerHandler;
        this.getUserProfileHandler = getUserProfileHandler;
        this.updateUserProfileHandler = updateUserProfileHandler;
        this.getUserRatingsHandler = getUserRatingsHandler;
        this.getUserFavoritesHandler = getUserFavoritesHandler;
        this.getRecommendationsHandler = getRecommendationsHandler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // Router logic
        if ("/api/users/login".equals(path) && "POST".equalsIgnoreCase(method)) {
            loginHandler.handle(exchange);
        } else if ("/api/users/register".equals(path) && "POST".equalsIgnoreCase(method)) {
            registerHandler.handle(exchange);
        } else if (path.matches("^/api/users/\\d+/profile$")) {
            if ("GET".equalsIgnoreCase(method)) {
                getUserProfileHandler.handle(exchange);
            } else if ("PUT".equalsIgnoreCase(method)) {
                updateUserProfileHandler.handle(exchange);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } else if (path.matches("^/api/users/\\d+/ratings$") && "GET".equalsIgnoreCase(method)) {
            getUserRatingsHandler.handle(exchange);
        } else if (path.matches("^/api/users/\\d+/favorites$") && "GET".equalsIgnoreCase(method)) {
            getUserFavoritesHandler.handle(exchange);
        } else if (path.matches("^/api/users/\\d+/recommendations$") && "GET".equalsIgnoreCase(method)) {
            getRecommendationsHandler.handle(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}
