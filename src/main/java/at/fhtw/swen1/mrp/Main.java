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
import at.fhtw.swen1.mrp.handler.LeaderboardHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        DatabaseManager.getInstance();

        int port = 8080;
        startServer(port);
        System.out.println("Server started on port " + port);
    }

    public static HttpServer startServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        UserRepository userRepository = new UserRepository();
        RatingRepository ratingRepository = new RatingRepository();
        MediaRepository mediaRepository = new MediaRepository();
        FavoriteRepository favoriteRepository = new FavoriteRepository();

        UserService userService = new UserService(userRepository, mediaRepository);
        RatingService ratingService = new RatingService(ratingRepository, mediaRepository);
        MediaService mediaService = new MediaService(mediaRepository);
        FavoriteService favoriteService = new FavoriteService(favoriteRepository, mediaRepository);
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        UserHandler userHandler = new UserHandler(userService, ratingService, favoriteService, objectMapper);
        RatingHandler ratingHandler = new RatingHandler(ratingService, userService, objectMapper);
        MediaHandler mediaHandler = new MediaHandler(mediaService, userService, ratingService, favoriteService, objectMapper);
        LeaderboardHandler leaderboardHandler = new LeaderboardHandler(ratingService, objectMapper);

        server.createContext("/api/users/register", userHandler);
        server.createContext("/api/users/login", userHandler);
        server.createContext("/api/users", userHandler); 
        
        server.createContext("/api/media", mediaHandler);
        server.createContext("/api/ratings", ratingHandler);
        server.createContext("/api/leaderboard", leaderboardHandler);

        server.setExecutor(null);
        server.start();
        return server;
    }
}
