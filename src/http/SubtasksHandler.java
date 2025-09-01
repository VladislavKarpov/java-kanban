package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager manager;
    private Gson gson;

    public SubtasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equals(method)) {
                if (path.matches("/subtasks/\\d+")) {
                    int id = Integer.parseInt(path.split("/")[2]);
                    Subtask subtask = manager.getSubtask(id);
                    if (subtask == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    sendText(exchange, gson.toJson(subtask), 200);
                } else {
                    List<Subtask> subtasks = manager.getAllSubtasks();
                    sendText(exchange, gson.toJson(subtasks), 200);
                }
            } else if ("POST".equals(method)) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Subtask subtask = gson.fromJson(body, Subtask.class);
                if (subtask.getId() == 0) {
                    manager.addSubtask(subtask);
                } else {
                    manager.updateSubtask(subtask);
                }
                sendText(exchange, "{}", 201);
            } else if ("DELETE".equals(method)) {
                if (path.matches("/subtasks/\\d+")) {
                    int id = Integer.parseInt(path.split("/")[2]);
                    manager.deleteSubtask(id);
                } else {
                    manager.deleteAllSubtasks();
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
