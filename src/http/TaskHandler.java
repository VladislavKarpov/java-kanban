package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager manager;
    private Gson gson;

    public TaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equals(method)) {
                if (path.matches("/tasks/\\d+")) {
                    int id = Integer.parseInt(path.split("/")[2]);
                    Task task = manager.getTask(id);
                    if (task == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    sendText(exchange, gson.toJson(task), 200);
                } else {
                    List<Task> tasks = manager.getAllTasks();
                    sendText(exchange, gson.toJson(tasks), 200);
                }
            } else if ("POST".equals(method)) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);
                if (task.getId() == 0) {
                    manager.addTask(task);
                } else {
                    manager.updateTask(task);
                }
                sendText(exchange, "{}", 201);
            } else if ("DELETE".equals(method)) {
                if (path.matches("/tasks/\\d+")) {
                    int id = Integer.parseInt(path.split("/")[2]);
                    manager.deleteTask(id);
                } else {
                    manager.deleteAllTasks();
                }
                sendText(exchange, "{}", 201);
            } else {
                sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
            }
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (Exception e) {
            sendServerError(exchange, e);
        }
    }
}
