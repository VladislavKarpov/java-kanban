package http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer server;


    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        Gson gson = getGson();
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        Map<String, HttpHandler> contexts = Map.of(
                "/tasks", new TaskHandler(manager, gson),
                "/subtasks", new SubtasksHandler(manager, gson),
                "/epics", new EpicsHandler(manager, gson),
                "/history", new HistoryHandler(manager, gson),
                "/prioritized", new PrioritizedHandler(manager, gson)
        );
        contexts.forEach(server::createContext);
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class,
                        (JsonSerializer<Duration>) (duration, type, context) ->
                                new JsonPrimitive(duration.toMinutes()))
                .registerTypeAdapter(Duration.class,
                        (JsonDeserializer<Duration>) (json, type, context) ->
                                Duration.ofMinutes(json.getAsLong()))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (dateTime, type, context) ->
                                new JsonPrimitive(dateTime.toString()))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, context) ->
                                LocalDateTime.parse(json.getAsString()))
                .create();
    }

    public static void main(String[] args) throws IOException {
        try {
            HttpTaskServer server = new HttpTaskServer();
            server.start();
        } catch (IOException e) {
            System.err.println("Не удалось запустить HTTP-сервер: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
