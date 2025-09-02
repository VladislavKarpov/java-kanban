package http;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTasksHandlerTest {

    private static final String BASE_URL = "http://localhost:8080/tasks";
    private HttpTaskServer server;
    private TaskManager manager;
    private Gson gson;
    private HttpClient httpClient;

    @BeforeEach
    public void setUp() throws Exception {
        // ✅ используем новый InMemoryTaskManager для "чистой" базы задач
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        gson = HttpTaskServer.getGson();
        httpClient = HttpClient.newHttpClient();
    }

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.stop(); // ✅ обязательно останавливаем сервер, иначе зависнет порт
        }
    }

    @Test
    void testAddTask() throws Exception {
        Task task = new Task("Test Task", "Desc");
        task.setStatus(TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());

        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json") // ✅ добавляем заголовок
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Ожидался статус 201 при создании задачи");
        assertEquals(1, manager.getAllTasks().size(), "В менеджере должна быть 1 задача");
    }

    @Test
    void testGetTasksEmpty() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200");
        assertTrue(response.body().isEmpty() || response.body().equals("[]"),
                "Ожидался пустой список задач");
    }

    @Test
    void testGetTaskByIdNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/999")) // ✅ исправлено, теперь правильный URL
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404 для несуществующей задачи");
    }

}
