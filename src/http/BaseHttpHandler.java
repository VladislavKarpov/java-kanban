package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected final TaskManager manager;
    protected final Gson gson;

    protected BaseHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendOk(HttpExchange exchange, String body) throws IOException {
        sendText(exchange, body, 200);
    }

    protected void sendCreated(HttpExchange exchange, String body) throws IOException {
        sendText(exchange, body, 201);
    }

    protected void sendNoContent(HttpExchange exchange) throws IOException {
        sendText(exchange, "", 204);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Not Found\"}", 404);
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
    }

    protected void sendHasInteractions(HttpExchange exchange, String msg) throws IOException {
        sendText(exchange, "{\"error\":\"" + msg + "\"}", 406);
    }

    protected void sendServerError(HttpExchange exchange, Exception e) throws IOException {
        sendText(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 500);
    }
}
