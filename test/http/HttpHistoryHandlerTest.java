package http;

import org.junit.jupiter.api.Test;
import task.Task;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpHistoryHandlerTest extends BaseHttpHandlerTest {

    @Test
    void shouldReturnEmptyHistory() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/history"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void shouldReturnNonEmptyHistory() throws Exception {
        Task task = new Task("Task 1", "Desc");
        manager.addTask(task);
        manager.getTask(task.getId()); // просмотр -> добавление в историю

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/history"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task 1"));
    }
}