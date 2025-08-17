package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTwoTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setup() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void cleanup() {
        tempFile.delete();
    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        manager.save();  // вручную вызываем сохранение

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
        assertTrue(loaded.getHistory().isEmpty());
    }

    @Test
    void shouldSaveAndLoadMultipleTasks() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        manager.addTask(task1);
        manager.addTask(task2);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> loadedTasks = loaded.getAllTasks();
        assertEquals(2, loadedTasks.size());
        assertEquals("Task 1", loadedTasks.get(0).getName());
        assertEquals("Task 2", loadedTasks.get(1).getName());
    }

    @Test
    void shouldSaveAndLoadEpicWithSubtask() {
        Epic epic = new Epic("Epic", "Big task");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Part of epic", epic.getId(), Status.NEW);
        manager.addSubtask(subtask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loaded.getAllEpics().size());
        assertEquals(1, loaded.getAllSubtasks().size());

        Subtask loadedSubtask = loaded.getAllSubtasks().get(0);
        assertEquals(epic.getId(), loadedSubtask.getEpicId());
    }

    @Nested
    class ManagersTest {
        @Test
        void managersShouldReturnInitializedInstances() {
            TaskManager manager = Managers.getDefault();
            HistoryManager history = Managers.getDefaultHistory();

            assertNotNull(manager);
            assertNotNull(history);
        }
        //Managers возвращает проинициализированные объекты

    }
}