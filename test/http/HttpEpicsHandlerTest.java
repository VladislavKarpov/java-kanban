package http;

import org.junit.jupiter.api.Test;
import task.Epic;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    }

    @Test
    void shouldGetEpicSubtasks() throws Exception {
        Epic epic = new Epic("Epic 1", "Epic Desc");
        manager.addEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics/" + epic.getId() + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}
