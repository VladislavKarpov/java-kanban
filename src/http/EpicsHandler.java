package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Epic;
import task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager manager, Gson gson) {
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
        if (path.matches("/epics/\\d+/subtasks")) {
            int id = Integer.parseInt(path.split("/")[2]);
            List<Subtask> subtasks = manager.getSubtasksOfEpic(id);
            sendOk(exchange, gson.toJson(subtasks));
        } else if (path.matches("/epics/\\d+")) {
            int id = Integer.parseInt(path.split("/")[2]);
            Epic epic = manager.getEpic(id);
            if (epic == null) {
                sendNotFound(exchange);
            } else {
                sendOk(exchange, gson.toJson(epic));
            }
        } else {
            List<Epic> epics = manager.getAllEpics();
            sendOk(exchange, gson.toJson(epics));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);

        if (epic.getId() == 0) {
            manager.addEpic(epic);
        } else {
            manager.updateEpic(epic);
        }

        sendCreated(exchange, "{}");
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/epics/\\d+")) {
            int id = Integer.parseInt(path.split("/")[2]);
            manager.deleteEpic(id);
        } else {
            manager.deleteAllEpics();
        }
        sendNoContent(exchange);
    }
}