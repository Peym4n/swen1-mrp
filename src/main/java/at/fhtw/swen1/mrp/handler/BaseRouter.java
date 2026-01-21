package at.fhtw.swen1.mrp.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;

public abstract class BaseRouter implements HttpHandler {

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String resp = "{\"error\": \"Not Found\"}";
        sendResponse(exchange, 404, resp);
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        String resp = "{\"error\": \"Method Not Allowed\"}";
        sendResponse(exchange, 405, resp);
    }

    protected void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}
