package at.fhtw.swen1.mrp.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Router for media-related requests.
 */
public final class MediaRouter extends BaseRouter {
    /** Handler for retrieving media. */
    private final GetMediaHandler getMediaHandler;
    /** Handler for creating media. */
    private final CreateMediaHandler createMediaHandler;
    /** Handler for retrieving media by ID. */
    private final GetMediaByIdHandler getMediaByIdHandler;
    /** Handler for updating media. */
    private final UpdateMediaHandler updateMediaHandler;
    /** Handler for deleting media. */
    private final DeleteMediaHandler deleteMediaHandler;
    /** Handler for rating media. */
    private final RateMediaHandler rateMediaHandler;
    /** Handler for adding favorite media. */
    private final AddFavoriteMediaHandler addFavoriteMediaHandler;
    /** Handler for removing favorite media. */
    private final RemoveFavoriteMediaHandler removeFavoriteMediaHandler;

    /**
     * Constructor.
     *
     * @param getMediaHandlerArg the get media handler
     * @param createMediaHandlerArg the create media handler
     * @param getMediaByIdHandlerArg the get media by ID handler
     * @param updateMediaHandlerArg the update media handler
     * @param deleteMediaHandlerArg the delete media handler
     * @param rateMediaHandlerArg the rate media handler
     * @param addFavMediaHandlerArg the add favorite media handler
     * @param removeFavMediaHandlerArg the remove favorite media handler
     */
    // CHECKSTYLE:OFF: ParameterNumber
    public MediaRouter(final GetMediaHandler getMediaHandlerArg,
                       final CreateMediaHandler createMediaHandlerArg,
                       final GetMediaByIdHandler getMediaByIdHandlerArg,
                       final UpdateMediaHandler updateMediaHandlerArg,
                       final DeleteMediaHandler deleteMediaHandlerArg,
                       final RateMediaHandler rateMediaHandlerArg,
                       final AddFavoriteMediaHandler addFavMediaHandlerArg,
                       final RemoveFavoriteMediaHandler removeFavMediaHandlerArg) {
        // CHECKSTYLE:ON: ParameterNumber
        this.getMediaHandler = getMediaHandlerArg;
        this.createMediaHandler = createMediaHandlerArg;
        this.getMediaByIdHandler = getMediaByIdHandlerArg;
        this.updateMediaHandler = updateMediaHandlerArg;
        this.deleteMediaHandler = deleteMediaHandlerArg;
        this.rateMediaHandler = rateMediaHandlerArg;
        this.addFavoriteMediaHandler = addFavMediaHandlerArg;
        this.removeFavoriteMediaHandler = removeFavMediaHandlerArg;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
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
