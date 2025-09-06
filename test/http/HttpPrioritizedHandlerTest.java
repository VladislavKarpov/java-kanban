package http;

import org.junit.jupiter.api.Test;
import task.Task;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpPrioritizedHandlerTest extends BaseHttpHandlerTest {

    @Test
    void shouldReturnEmptyPrioritizedTasks() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void shouldReturnPrioritizedTasks() throws Exception {
        Task task1 = new Task("Task 1", "Desc");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(10));

        Task task2 = new Task("Task 2", "Desc");
        task2.setStartTime(LocalDateTime.now().plusMinutes(20));
        task2.setDuration(Duration.ofMinutes(10));

        manager.addTask(task2);
        manager.addTask(task1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        String body = response.body();
        assertTrue(body.indexOf("Task 1") < body.indexOf("Task 2")); // сортировка
    }
}