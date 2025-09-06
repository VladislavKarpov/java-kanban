package http;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpSubtasksHandlerTest extends BaseHttpHandlerTest {

    @Test
    void shouldCreateSubtask() throws Exception {
        Epic epic = new Epic("Epic 1", "Epic Desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Desc", epic.getId());
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStartTime(LocalDateTime.now());

        String json = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        // Проверяем через GET
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertTrue(getResponse.body().contains("Subtask 1"));
    }

    @Test
    void shouldReturnAllSubtasks() throws Exception {
        Epic epic = new Epic("Epic 1", "Epic Desc");
        manager.addEpic(epic);

        manager.addSubtask(new Subtask("Subtask 1", "Desc", epic.getId()));
        manager.addSubtask(new Subtask("Subtask 2", "Desc", epic.getId()));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask 1"));
        assertTrue(response.body().contains("Subtask 2"));
    }

    @Test
    void shouldReturnSubtaskById() throws Exception {
        Epic epic = new Epic("Epic 1", "Epic Desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Desc", epic.getId());
        manager.addSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks/" + subtask.getId()))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask 1"));
    }

    @Test
    void shouldDeleteSubtaskById() throws Exception {
        Epic epic = new Epic("Epic 1", "Epic Desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Desc", epic.getId());
        manager.addSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks/" + subtask.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode());

        // Проверяем через GET
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks/" + subtask.getId()))
                .GET()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode());
    }

    @Test
    void shouldDeleteAllSubtasks() throws Exception {
        Epic epic = new Epic("Epic 1", "Epic Desc");
        manager.addEpic(epic);

        manager.addSubtask(new Subtask("Subtask 1", "Desc", epic.getId()));
        manager.addSubtask(new Subtask("Subtask 2", "Desc", epic.getId()));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals("[]", getResponse.body());
    }
}