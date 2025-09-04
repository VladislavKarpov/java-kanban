package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET" -> handleGet(exchange, path);
                case "POST" -> handlePost(exchange);
                case "DELETE" -> handleDelete(exchange, path);
                default -> sendMethodNotAllowed(exchange);
            }
        } catch (Exception e) {
            sendServerError(exchange, e);
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/subtasks/\\d+")) {
            int id = Integer.parseInt(path.split("/")[2]);
            Subtask subtask = manager.getSubtask(id);
            if (subtask == null) {
                sendNotFound(exchange);
            } else {
                sendOk(exchange, gson.toJson(subtask));
            }
        } else {
            List<Subtask> subtasks = manager.getAllSubtasks();
            sendOk(exchange, gson.toJson(subtasks));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        if (subtask.getId() == 0) {
            manager.addSubtask(subtask);
        } else {
            manager.updateSubtask(subtask);
        }

        sendCreated(exchange, "{}");
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/subtasks/\\d+")) {
            int id = Integer.parseInt(path.split("/")[2]);
            manager.deleteSubtask(id);
        } else {
            manager.deleteAllSubtasks();
        }
        sendNoContent(exchange);
    }
}