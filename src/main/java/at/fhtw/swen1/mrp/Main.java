package at.fhtw.swen1.mrp;

import at.fhtw.swen1.mrp.data.DatabaseManager;
import at.fhtw.swen1.mrp.handler.MediaHandler;
import at.fhtw.swen1.mrp.handler.UserHandler;
import at.fhtw.swen1.mrp.repository.MediaRepository;
import at.fhtw.swen1.mrp.repository.UserRepository;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.service.UserService;
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
        UserHandler userHandler = new UserHandler(userService);

        MediaRepository mediaRepository = new MediaRepository();
        MediaService mediaService = new MediaService(mediaRepository);
        MediaHandler mediaHandler = new MediaHandler(mediaService, userService);

        // Contexts
        server.createContext("/api/users/register", userHandler);
        server.createContext("/api/users/login", userHandler);
        server.createContext("/api/media", mediaHandler);

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }
}
