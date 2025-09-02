package http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Not Found\"}", 404);
    }

    protected void sendServerError(HttpExchange exchange, Exception e) throws IOException {
        sendText(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 500);
    }

    protected void sendHasInteractions(HttpExchange exchange, String message) throws IOException {
        String response = (message == null || message.isBlank())
                ? "Task has time intersection with another task"
                : message;
        sendText(exchange, response, 406);
    }
}
