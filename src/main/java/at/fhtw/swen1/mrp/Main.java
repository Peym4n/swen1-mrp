package at.fhtw.swen1.mrp;

import at.fhtw.swen1.mrp.data.DatabaseManager;
import at.fhtw.swen1.mrp.handler.MediaHandler;
import at.fhtw.swen1.mrp.handler.UserHandler;
import at.fhtw.swen1.mrp.repository.MediaRepository;
import at.fhtw.swen1.mrp.repository.UserRepository;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.RatingService;
import at.fhtw.swen1.mrp.service.UserService;
import at.fhtw.swen1.mrp.handler.RatingHandler;
import at.fhtw.swen1.mrp.repository.RatingRepository;
import at.fhtw.swen1.mrp.repository.FavoriteRepository;
import at.fhtw.swen1.mrp.service.FavoriteService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        // Initialize DB
        DatabaseManager.getInstance();

        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Repositories
        UserRepository userRepository = new UserRepository();
        RatingRepository ratingRepository = new RatingRepository();
        MediaRepository mediaRepository = new MediaRepository();
        FavoriteRepository favoriteRepository = new FavoriteRepository();

        // Services
        UserService userService = new UserService(userRepository);
        RatingService ratingService = new RatingService(ratingRepository);
        MediaService mediaService = new MediaService(mediaRepository);
        FavoriteService favoriteService = new FavoriteService(favoriteRepository, mediaRepository);
        
        // Handlers
        UserHandler userHandler = new UserHandler(userService, ratingService, favoriteService);
        RatingHandler ratingHandler = new RatingHandler(ratingService, userService);
        // Note: mediaHandler checks path for /rate and /favorite
        MediaHandler mediaHandler = new MediaHandler(mediaService, userService, ratingService, favoriteService);

        // Contexts
        server.createContext("/api/users/register", userHandler);
        server.createContext("/api/users/login", userHandler);
        server.createContext("/api/media", mediaHandler);
        server.createContext("/api/ratings", ratingHandler);

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }
}
