package http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private final TaskManager manager;
    private final HttpServer server;
    private static final Gson gson = createGson();
    private static final int DEFAULT_PORT = 8080;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this(manager, DEFAULT_PORT);
    }

    public HttpTaskServer(TaskManager manager, int DEFAULT_PORT) throws IOException {
        this.manager = manager;
        this.server = initServer();
    }

    private HttpServer initServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(DEFAULT_PORT), 0);

        server.createContext("/tasks", new TaskHandler(manager, gson));
        server.createContext("/subtasks", new SubtasksHandler(manager, gson));
        server.createContext("/epics", new EpicsHandler(manager, gson));
        server.createContext("/history", new HistoryHandler(manager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(manager, gson));

        return server;
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + DEFAULT_PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    public int getPort() {
        return server.getAddress().getPort();
    }

    public static Gson getGson() {
        return gson;
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class,
                        (JsonSerializer<Duration>) (duration, type, context) -> new JsonPrimitive(duration.toMillis()))
                .registerTypeAdapter(Duration.class,
                        (JsonDeserializer<Duration>) (element, type, context) -> Duration.ofMillis(element.getAsLong()))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (dateTime, type, context) -> new JsonPrimitive(dateTime.toString()))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (element, type, context) -> LocalDateTime.parse(element.getAsString()))
                .create();
    }

    public static void main(String[] args) {
        try {
            TaskManager manager = Managers.getDefault();
            HttpTaskServer server = new HttpTaskServer(manager, 8080);
            server.start();
        } catch (IOException e) {
            System.err.println("Не удалось запустить HTTP-сервер: " + e.getMessage());
        }
    }
}