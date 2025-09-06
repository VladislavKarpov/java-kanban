package http;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.TaskStatus;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpEpicsHandlerTest extends BaseHttpHandlerTest {

    @Test
    void shouldCreateEpic() throws Exception {
        Epic epic = new Epic("Epic 1", "Epic Desc");
        String json = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpics().size());

        Epic createdEpic = manager.getAllEpics().get(0);
        assertEquals(epic.getName(), createdEpic.getName(), "Имя эпиков не совпадает");
        assertEquals(epic.getDescription(), createdEpic.getDescription(), "Описание эпиков не совпадает");
        assertEquals(TaskStatus.NEW, createdEpic.getStatus(), "Статус эпика должен быть NEW");
        assertTrue(createdEpic.getId() > 0, "ID эпика не должен быть 0");
    }

    @Test
    void shouldReturnAllEpics() throws Exception {
        manager.addEpic(new Epic("Epic 1", "Desc"));
        manager.addEpic(new Epic("Epic 2", "Desc"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Epic 1"));
        assertTrue(response.body().contains("Epic 2"));
    }

    @Test
    void shouldReturnEpicById() throws Exception {
        Epic epic = new Epic("Epic 1", "Desc");
        manager.addEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics/" + epic.getId()))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Epic 1"));
    }

    @Test
    void shouldDeleteEpicById() throws Exception {
        Epic epic = new Epic("Epic 1", "Desc");
        manager.addEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics/" + epic.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode());
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    void shouldDeleteAllEpics() throws Exception {
        manager.addEpic(new Epic("Epic 1", "Desc"));
        manager.addEpic(new Epic("Epic 2", "Desc"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode());
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    void shouldReturn404WhenEpicNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics/999"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("{\"error\":\"Not Found\"}", response.body());
    }
}