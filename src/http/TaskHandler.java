package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager manager, Gson gson) {
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
        if (path.matches("/tasks/\\d+")) {
            int id = Integer.parseInt(path.split("/")[2]);
            Task task = manager.getTask(id);
            if (task == null) {
                sendNotFound(exchange);
            } else {
                sendOk(exchange, gson.toJson(task));
            }
        } else {
            List<Task> tasks = manager.getAllTasks();
            sendOk(exchange, gson.toJson(tasks));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);

        if (task.getId() == 0) {
            manager.addTask(task);
        } else {
            manager.updateTask(task);
        }

        sendCreated(exchange, "{}");
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/tasks/\\d+")) {
            int id = Integer.parseInt(path.split("/")[2]);
            manager.deleteTask(id);
        } else {
            manager.deleteAllTasks();
        }
        sendNoContent(exchange);
    }
}