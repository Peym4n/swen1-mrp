package at.fhtw.swen1.mrp.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

/**
 * Base router providing common routing functionality.
 */
public abstract class BaseRouter implements HttpHandler {

    /**
     * Sends a 404 Not Found response.
     *
     * @param exchange the HTTP exchange
     * @throws IOException if an I/O error occurs
     */
    protected void sendNotFound(final HttpExchange exchange) throws IOException {
        String resp = "{\"error\": \"Not Found\"}";
        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, resp);
    }

    /**
     * Sends a 405 Method Not Allowed response.
     *
     * @param exchange the HTTP exchange
     * @throws IOException if an I/O error occurs
     */
    protected void sendMethodNotAllowed(final HttpExchange exchange) throws IOException {
        String resp = "{\"error\": \"Method Not Allowed\"}";
        sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, resp);
    }

    /**
     * Sends an HTTP response.
     *
     * @param exchange the HTTP exchange
     * @param statusCode the HTTP status code
     * @param response the response body
     * @throws IOException if an I/O error occurs
     */
    protected void sendResponse(final HttpExchange exchange, final int statusCode, final String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}
