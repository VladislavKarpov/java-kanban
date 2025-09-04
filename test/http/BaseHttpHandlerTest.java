package http;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.net.http.HttpClient;

public abstract class BaseHttpHandlerTest {

    protected static final String BASE_URL = "http://localhost:8080";
    protected HttpTaskServer server;
    protected TaskManager manager;
    protected Gson gson;
    protected HttpClient httpClient;

    @BeforeEach
    public void setUp() throws Exception {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        gson = HttpTaskServer.getGson();
        httpClient = HttpClient.newHttpClient();
    }

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.stop();
        }
    }
}