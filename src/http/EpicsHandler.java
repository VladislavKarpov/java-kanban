package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager manager;
    private Gson gson;

    public EpicsHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equals(method)) {
                if (path.matches("/epics/\\d+")) {
                    int id = Integer.parseInt(path.split("/")[2]);
                    Epic epic = manager.getEpic(id);
                    if (epic == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    sendText(exchange, gson.toJson(epic), 200);
                } else {
                    List<Epic> epics = manager.getAllEpics();
                    sendText(exchange, gson.toJson(epics), 200);
                }
            } else if ("POST".equals(method)) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);
                if (epic.getId() == 0) {
                    manager.addEpic(epic);
                } else {
                    manager.updateEpic(epic);
                }
                sendText(exchange, "{}", 201);
            } else if ("DELETE".equals(method)) {
                if (path.matches("/epics/\\d+")) {
                    int id = Integer.parseInt(path.split("/")[2]);
                    manager.deleteEpic(id);
                } else {
                    manager.deleteAllEpics();
                }
                sendText(exchange, "{}", 201);
            } else {
                sendText(exchange, "{\"error\":\"Method Not Allowed\"}", 405);
            }
        } catch (Exception e) {
            sendServerError(exchange, e);
        }
    }
}
