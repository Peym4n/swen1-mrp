package at.fhtw.swen1.mrp;

import at.fhtw.swen1.mrp.data.DatabaseManager;
import at.fhtw.swen1.mrp.handler.MediaHandler;
import at.fhtw.swen1.mrp.handler.UserHandler;
import at.fhtw.swen1.mrp.repository.MediaRepository;
import at.fhtw.swen1.mrp.repository.UserRepository;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.UserService;
import at.fhtw.swen1.mrp.handler.RatingHandler;
import at.fhtw.swen1.mrp.repository.RatingRepository;
import at.fhtw.swen1.mrp.service.RatingService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        // Initialize DB
        DatabaseManager.getInstance();

        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Wiring
        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        
        // Rating
        RatingRepository ratingRepository = new RatingRepository();
        RatingService ratingService = new RatingService(ratingRepository);
        
        // Handlers
        UserHandler userHandler = new UserHandler(userService, ratingService);
        RatingHandler ratingHandler = new RatingHandler(ratingService, userService);

        MediaRepository mediaRepository = new MediaRepository();
        MediaService mediaService = new MediaService(mediaRepository);
        // Note: mediaHandler checks path for /rate and uses ratingService
        MediaHandler mediaHandler = new MediaHandler(mediaService, userService, ratingService);

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
