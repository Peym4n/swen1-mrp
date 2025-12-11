package at.fhtw.swen1.mrp;

import at.fhtw.swen1.mrp.handler.MediaHandler;
import at.fhtw.swen1.mrp.service.MediaService;
import at.fhtw.swen1.mrp.data.DatabaseConnection;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        DatabaseConnection.executeInitScript();
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        MediaService mediaService = new MediaService();
        server.createContext("/api/media", new MediaHandler(mediaService));

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }
}
