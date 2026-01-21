package at.fhtw.swen1.mrp.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class RatingRouter extends BaseRouter {
    private final UpdateRatingHandler updateRatingHandler;
    private final DeleteRatingHandler deleteRatingHandler;
    private final ConfirmRatingHandler confirmRatingHandler;
    private final LikeRatingHandler likeRatingHandler;

    public RatingRouter(UpdateRatingHandler updateRatingHandler, DeleteRatingHandler deleteRatingHandler,
                         ConfirmRatingHandler confirmRatingHandler, LikeRatingHandler likeRatingHandler) {
        this.updateRatingHandler = updateRatingHandler;
        this.deleteRatingHandler = deleteRatingHandler;
        this.confirmRatingHandler = confirmRatingHandler;
        this.likeRatingHandler = likeRatingHandler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
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
        } else if (path.matches("^/api/ratings/\\d+/confirm$") && "POST".equalsIgnoreCase(method)) {
            confirmRatingHandler.handle(exchange);
        } else if (path.matches("^/api/ratings/\\d+/like$") && "POST".equalsIgnoreCase(method)) {
            likeRatingHandler.handle(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}
