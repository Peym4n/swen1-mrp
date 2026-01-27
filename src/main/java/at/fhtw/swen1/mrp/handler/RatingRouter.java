package at.fhtw.swen1.mrp.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Router for rating-related requests.
 */
public final class RatingRouter extends BaseRouter {
    /** Handler for updating ratings. */
    private final UpdateRatingHandler updateRatingHandler;
    /** Handler for deleting ratings. */
    private final DeleteRatingHandler deleteRatingHandler;
    /** Handler for confirming ratings. */
    private final ConfirmRatingHandler confirmRatingHandler;
    /** Handler for liking ratings. */
    private final LikeRatingHandler likeRatingHandler;

    /**
     * Constructor.
     *
     * @param updateRatingHandlerArg the update rating handler
     * @param deleteRatingHandlerArg the delete rating handler
     * @param confirmRatingHandlerArg the confirm rating handler
     * @param likeRatingHandlerArg the like rating handler
     */
    public RatingRouter(final UpdateRatingHandler updateRatingHandlerArg,
                        final DeleteRatingHandler deleteRatingHandlerArg,
                        final ConfirmRatingHandler confirmRatingHandlerArg,
                        final LikeRatingHandler likeRatingHandlerArg) {
        this.updateRatingHandler = updateRatingHandlerArg;
        this.deleteRatingHandler = deleteRatingHandlerArg;
        this.confirmRatingHandler = confirmRatingHandlerArg;
        this.likeRatingHandler = likeRatingHandlerArg;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (path.matches("^/api/ratings/\\d+$")) {
            if ("PUT".equalsIgnoreCase(method)) {
                updateRatingHandler.handle(exchange);
            } else if ("DELETE".equalsIgnoreCase(method)) {
                deleteRatingHandler.handle(exchange);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } else if (path.matches("^/api/ratings/\\d+/confirm$")
                && "POST".equalsIgnoreCase(method)) {
            confirmRatingHandler.handle(exchange);
        } else if (path.matches("^/api/ratings/\\d+/like$")
                && "POST".equalsIgnoreCase(method)) {
            likeRatingHandler.handle(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}
