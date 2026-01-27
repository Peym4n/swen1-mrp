package at.fhtw.swen1.mrp.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Router for user-related requests.
 */
public final class UserRouter extends BaseRouter {
    /** Handler for login. */
    private final LoginHandler loginHandler;
    /** Handler for registration. */
    private final RegisterHandler registerHandler;
    /** Handler for retrieving user profile. */
    private final GetUserProfileHandler getUserProfileHandler;
    /** Handler for updating user profile. */
    private final UpdateUserProfileHandler updateUserProfileHandler;
    /** Handler for retrieving user ratings. */
    private final GetUserRatingsHandler getUserRatingsHandler;
    /** Handler for retrieving user favorites. */
    private final GetUserFavoritesHandler getUserFavoritesHandler;
    /** Handler for retrieving recommendations. */
    private final GetRecommendationsHandler getRecommendationsHandler;

    /**
     * Constructor.
     *
     * @param loginHandlerArg the login handler
     * @param registerHandlerArg the register handler
     * @param getUserProfileHandlerArg the get user profile handler
     * @param updateUserProfileHandlerArg the update user profile handler
     * @param getUserRatingsHandlerArg the get user ratings handler
     * @param getUserFavoritesHandlerArg the get user favorites handler
     * @param getRecHandlerArg the get recommendations handler
     */
    public UserRouter(final LoginHandler loginHandlerArg,
                      final RegisterHandler registerHandlerArg,
                      final GetUserProfileHandler getUserProfileHandlerArg,
                      final UpdateUserProfileHandler
                              updateUserProfileHandlerArg,
                      final GetUserRatingsHandler getUserRatingsHandlerArg,
                      final GetUserFavoritesHandler getUserFavoritesHandlerArg,
                      final GetRecommendationsHandler getRecHandlerArg) {
        this.loginHandler = loginHandlerArg;
        this.registerHandler = registerHandlerArg;
        this.getUserProfileHandler = getUserProfileHandlerArg;
        this.updateUserProfileHandler = updateUserProfileHandlerArg;
        this.getUserRatingsHandler = getUserRatingsHandlerArg;
        this.getUserFavoritesHandler = getUserFavoritesHandlerArg;
        this.getRecommendationsHandler = getRecHandlerArg;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // Router logic
        if ("/api/users/login".equals(path) && "POST".equalsIgnoreCase(method)) {
            loginHandler.handle(exchange);
        } else if ("/api/users/register".equals(path)
                && "POST".equalsIgnoreCase(method)) {
            registerHandler.handle(exchange);
        } else if (path.matches("^/api/users/\\d+/profile$")) {
            if ("GET".equalsIgnoreCase(method)) {
                getUserProfileHandler.handle(exchange);
            } else if ("PUT".equalsIgnoreCase(method)) {
                updateUserProfileHandler.handle(exchange);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } else if (path.matches("^/api/users/\\d+/ratings$")
                && "GET".equalsIgnoreCase(method)) {
            getUserRatingsHandler.handle(exchange);
        } else if (path.matches("^/api/users/\\d+/favorites$")
                && "GET".equalsIgnoreCase(method)) {
            getUserFavoritesHandler.handle(exchange);
        } else if (path.matches("^/api/users/\\d+/recommendations$")
                && "GET".equalsIgnoreCase(method)) {
            getRecommendationsHandler.handle(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}
