package at.fhtw.swen1.mrp.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class MediaRouter extends BaseRouter {
    private final GetMediaHandler getMediaHandler;
    private final CreateMediaHandler createMediaHandler;
    private final GetMediaByIdHandler getMediaByIdHandler;
    private final UpdateMediaHandler updateMediaHandler;
    private final DeleteMediaHandler deleteMediaHandler;
    private final RateMediaHandler rateMediaHandler;
    private final AddFavoriteMediaHandler addFavoriteMediaHandler;
    private final RemoveFavoriteMediaHandler removeFavoriteMediaHandler;

    public MediaRouter(GetMediaHandler getMediaHandler, CreateMediaHandler createMediaHandler,
                        GetMediaByIdHandler getMediaByIdHandler, UpdateMediaHandler updateMediaHandler,
                        DeleteMediaHandler deleteMediaHandler, RateMediaHandler rateMediaHandler,
                        AddFavoriteMediaHandler addFavoriteMediaHandler, RemoveFavoriteMediaHandler removeFavoriteMediaHandler) {
        this.getMediaHandler = getMediaHandler;
        this.createMediaHandler = createMediaHandler;
        this.getMediaByIdHandler = getMediaByIdHandler;
        this.updateMediaHandler = updateMediaHandler;
        this.deleteMediaHandler = deleteMediaHandler;
        this.rateMediaHandler = rateMediaHandler;
        this.addFavoriteMediaHandler = addFavoriteMediaHandler;
        this.removeFavoriteMediaHandler = removeFavoriteMediaHandler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("/api/media".equals(path)) {
            if ("GET".equalsIgnoreCase(method)) {
                getMediaHandler.handle(exchange);
            } else if ("POST".equalsIgnoreCase(method)) {
                createMediaHandler.handle(exchange);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } else if (path.matches("^/api/media/\\d+$")) {
            if ("GET".equalsIgnoreCase(method)) {
                getMediaByIdHandler.handle(exchange);
            } else if ("PUT".equalsIgnoreCase(method)) {
                updateMediaHandler.handle(exchange);
            } else if ("DELETE".equalsIgnoreCase(method)) {
                deleteMediaHandler.handle(exchange);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } else if (path.matches("^/api/media/\\d+/rate$")) {
            if ("POST".equalsIgnoreCase(method)) {
                rateMediaHandler.handle(exchange);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } else if (path.matches("^/api/media/\\d+/favorite$")) {
            if ("POST".equalsIgnoreCase(method)) {
                addFavoriteMediaHandler.handle(exchange);
            } else if ("DELETE".equalsIgnoreCase(method)) {
                removeFavoriteMediaHandler.handle(exchange);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}
