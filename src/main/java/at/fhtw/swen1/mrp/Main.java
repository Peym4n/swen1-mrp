package at.fhtw.swen1.mrp;

import at.fhtw.swen1.mrp.data.DatabaseManager;
import at.fhtw.swen1.mrp.handler.AddFavoriteMediaHandler;
import at.fhtw.swen1.mrp.handler.ConfirmRatingHandler;
import at.fhtw.swen1.mrp.handler.CreateMediaHandler;
import at.fhtw.swen1.mrp.handler.DeleteMediaHandler;
import at.fhtw.swen1.mrp.handler.DeleteRatingHandler;
import at.fhtw.swen1.mrp.handler.GetLeaderboardHandler;
import at.fhtw.swen1.mrp.handler.GetMediaByIdHandler;
import at.fhtw.swen1.mrp.handler.GetMediaHandler;
import at.fhtw.swen1.mrp.handler.GetRecommendationsHandler;
import at.fhtw.swen1.mrp.handler.GetUserFavoritesHandler;
import at.fhtw.swen1.mrp.handler.GetUserProfileHandler;
import at.fhtw.swen1.mrp.handler.GetUserRatingsHandler;
import at.fhtw.swen1.mrp.handler.LikeRatingHandler;
import at.fhtw.swen1.mrp.handler.LoginHandler;
import at.fhtw.swen1.mrp.handler.MediaRouter;
import at.fhtw.swen1.mrp.handler.RateMediaHandler;
import at.fhtw.swen1.mrp.handler.RatingRouter;
import at.fhtw.swen1.mrp.handler.RegisterHandler;
import at.fhtw.swen1.mrp.handler.RemoveFavoriteMediaHandler;
import at.fhtw.swen1.mrp.handler.UpdateMediaHandler;
import at.fhtw.swen1.mrp.handler.UpdateRatingHandler;
import at.fhtw.swen1.mrp.handler.UpdateUserProfileHandler;
import at.fhtw.swen1.mrp.handler.UserRouter;
import at.fhtw.swen1.mrp.repository.FavoriteRepository;
import at.fhtw.swen1.mrp.repository.MediaRepository;
import at.fhtw.swen1.mrp.repository.RatingRepository;
import at.fhtw.swen1.mrp.repository.UserRepository;
import at.fhtw.swen1.mrp.service.FavoriteService;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Main class to start the application.
 */
public final class Main {

    /**
     * Private constructor to hide the implicit public one.
     */
    private Main() {
        // Utility class
    }
    /** Default port. */
    private static final int DEFAULT_PORT = 8080;

    /**
     * Main method.
     *
     * @param args command line arguments
     * @throws IOException if server fails to start
     */
    // CHECKSTYLE:OFF: MagicNumber
    // CHECKSTYLE:OFF: RegexpSinglelineJava
    public static void main(final String[] args) throws IOException {
        DatabaseManager.getInstance();

        int port = DEFAULT_PORT;
        startServer(port);
        System.out.println("Server started on port " + port);
    }
    // CHECKSTYLE:ON: RegexpSinglelineJava
    // CHECKSTYLE:ON: MagicNumber

    /**
     * Starts the HTTP server.
     *
     * @param port the port to listen on
     * @return the started HttpServer
     * @throws IOException if creation fails
     */
    public static HttpServer startServer(final int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Repositories
        UserRepository userRepository = new UserRepository();
        RatingRepository ratingRepository = new RatingRepository();
        MediaRepository mediaRepository = new MediaRepository();
        FavoriteRepository favoriteRepository = new FavoriteRepository();

        // Services
        UserService userService = new UserService(userRepository,
                mediaRepository);
        RatingService ratingService = new RatingService(ratingRepository,
                mediaRepository);
        MediaService mediaService = new MediaService(mediaRepository);
        FavoriteService favoriteService = new FavoriteService(
                favoriteRepository, mediaRepository);

        // Config
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // --- Granular Handlers ---

        // Auth & User
        LoginHandler loginHandler = new LoginHandler(userService, objectMapper);
        RegisterHandler registerHandler = new RegisterHandler(userService,
                objectMapper);
        GetUserProfileHandler getUserProfileHandler =
                new GetUserProfileHandler(userService, objectMapper);
        UpdateUserProfileHandler updateUserProfileHandler =
                new UpdateUserProfileHandler(userService, objectMapper);
        GetUserRatingsHandler getUserRatingsHandler =
                new GetUserRatingsHandler(userService, ratingService,
                        objectMapper);
        GetUserFavoritesHandler getUserFavoritesHandler =
                new GetUserFavoritesHandler(userService, favoriteService,
                        objectMapper);
        GetRecommendationsHandler getRecommendationsHandler =
                new GetRecommendationsHandler(userService, objectMapper);

        // Media
        GetMediaHandler getMediaHandler = new GetMediaHandler(userService,
                mediaService, objectMapper);
        CreateMediaHandler createMediaHandler = new CreateMediaHandler(
                userService, mediaService, objectMapper);
        GetMediaByIdHandler getMediaByIdHandler =
                new GetMediaByIdHandler(userService, mediaService,
                        objectMapper);
        UpdateMediaHandler updateMediaHandler = new UpdateMediaHandler(
                userService, mediaService, objectMapper);
        DeleteMediaHandler deleteMediaHandler = new DeleteMediaHandler(
                userService, mediaService, objectMapper);
        RateMediaHandler rateMediaHandler = new RateMediaHandler(userService,
                ratingService, objectMapper);
        AddFavoriteMediaHandler addFavoriteMediaHandler =
                new AddFavoriteMediaHandler(userService, favoriteService,
                        objectMapper);
        RemoveFavoriteMediaHandler removeFavoriteMediaHandler =
                new RemoveFavoriteMediaHandler(userService, favoriteService,
                        objectMapper);

        // Rating
        UpdateRatingHandler updateRatingHandler =
                new UpdateRatingHandler(userService, ratingService, objectMapper);
        DeleteRatingHandler deleteRatingHandler =
                new DeleteRatingHandler(userService, ratingService,
                        objectMapper);
        ConfirmRatingHandler confirmRatingHandler =
                new ConfirmRatingHandler(userService, ratingService, objectMapper);
        LikeRatingHandler likeRatingHandler = new LikeRatingHandler(userService,
                ratingService, objectMapper);

        // Leaderboard
        GetLeaderboardHandler getLeaderboardHandler =
                new GetLeaderboardHandler(userService, ratingService,
                        objectMapper);

        // --- Routers ---
        UserRouter userRouter = new UserRouter(
            loginHandler, registerHandler,
            getUserProfileHandler, updateUserProfileHandler,
            getUserRatingsHandler, getUserFavoritesHandler,
            getRecommendationsHandler
        );

        MediaRouter mediaRouter = new MediaRouter(
            getMediaHandler, createMediaHandler,
            getMediaByIdHandler, updateMediaHandler,
            deleteMediaHandler, rateMediaHandler,
            addFavoriteMediaHandler, removeFavoriteMediaHandler
        );

        RatingRouter ratingRouter = new RatingRouter(
            updateRatingHandler, deleteRatingHandler,
            confirmRatingHandler, likeRatingHandler
        );

        // Contexts
        server.createContext("/api/users", userRouter);
        server.createContext("/api/media", mediaRouter);
        server.createContext("/api/ratings", ratingRouter);
        server.createContext("/api/leaderboard", getLeaderboardHandler);

        server.setExecutor(null);
        server.start();
        return server;
    }
}
