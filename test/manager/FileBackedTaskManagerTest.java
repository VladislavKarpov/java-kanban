package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    public void setup() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    public void cleanup() {
        tempFile.delete();
    }

    @Test
    public void shouldSaveAndLoadEmptyManager() {
        manager.save();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getAllTasks().isEmpty(), "Tasks should be empty");
        assertTrue(loaded.getAllEpics().isEmpty(), "Epics should be empty");
        assertTrue(loaded.getAllSubtasks().isEmpty(), "Subtasks should be empty");
        assertTrue(loaded.getHistory().isEmpty(), "History should be empty");
    }

    @Test
    public void shouldSaveAndLoadEpicWithoutSubtasks() {
        Epic epic = new Epic("EpicWithoutSubs", "No subtasks here");
        manager.addEpic(epic);

        manager.save();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        List<Epic> loadedEpics = loaded.getAllEpics();
        assertEquals(1, loadedEpics.size());
        assertEquals(epic.getName(), loadedEpics.get(0).getName());
        assertTrue(loaded.getSubtasksOfEpic(epic.getId()).isEmpty(), "Epic should have no subtasks");
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        Task task = new Task("Task1", "Description");
        manager.addTask(task);

        Epic epic = new Epic("Epic1", "Description");
        manager.addEpic(epic);

        // Не вызываем getTask/getEpic - история остается пустой
        manager.save();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getHistory().isEmpty(), "History should remain empty");
    }

    @Override
    protected FileBackedTaskManager createManager() {
        try {
            File tempFile = File.createTempFile("tasks", ".csv");
            tempFile.deleteOnExit();
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    class EpicTaskStatusTests {

        @Test
        public void epicStatusShouldBeNewWhenAllSubtasksNew() {
            FileBackedTaskManager manager = createManager();
            Epic epic = new Epic("Epic", "Description");
            manager.addEpic(epic);

            Subtask sub1 = new Subtask("Sub1", "Desc", epic.getId(), TaskStatus.NEW);
            Subtask sub2 = new Subtask("Sub2", "Desc", epic.getId(), TaskStatus.NEW);
            manager.addSubtask(sub1);
            manager.addSubtask(sub2);

            assertEquals(TaskStatus.NEW, manager.getEpic(epic.getId()).getStatus());
        }

        @Test
        public void epicStatusShouldBeDoneWhenAllSubtasksDone() {
            FileBackedTaskManager manager = createManager();
            Epic epic = new Epic("Epic", "Description");
            manager.addEpic(epic);

            Subtask sub1 = new Subtask("Sub1", "Desc", epic.getId(), TaskStatus.DONE);
            Subtask sub2 = new Subtask("Sub2", "Desc", epic.getId(), TaskStatus.DONE);
            manager.addSubtask(sub1);
            manager.addSubtask(sub2);

            assertEquals(TaskStatus.DONE, manager.getEpic(epic.getId()).getStatus());
        }

        @Test
        public void epicStatusShouldBeInProgressWhenMixedNewAndDone() {
            FileBackedTaskManager manager = createManager();
            Epic epic = new Epic("Epic", "Description");
            manager.addEpic(epic);

            Subtask sub1 = new Subtask("Sub1", "Desc", epic.getId(), TaskStatus.NEW);
            Subtask sub2 = new Subtask("Sub2", "Desc", epic.getId(), TaskStatus.DONE);
            manager.addSubtask(sub1);
            manager.addSubtask(sub2);

            assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
        }

        @Test
        public void epicStatusShouldBeInProgressWhenSubtasksInProgress() {
            FileBackedTaskManager manager = createManager();
            Epic epic = new Epic("Epic", "Description");
            manager.addEpic(epic);

            Subtask sub1 = new Subtask("Sub1", "Desc", epic.getId(), TaskStatus.IN_PROGRESS);
            Subtask sub2 = new Subtask("Sub2", "Desc", epic.getId(), TaskStatus.IN_PROGRESS);
            manager.addSubtask(sub1);
            manager.addSubtask(sub2);

            assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
        }
    }

    @Nested
    class TaskOverlapTests {

        @Test
        public void shouldThrowExceptionWhenTasksOverlap() {
            FileBackedTaskManager manager = createManager();

            Task task1 = new Task("Task1", "Desc");
            task1.setStartTime(LocalDateTime.of(2025, 8, 19, 10, 0));
            task1.setDuration(Duration.ofHours(1));

            Task task2 = new Task("Task2", "Desc");
            task2.setStartTime(LocalDateTime.of(2025, 8, 19, 10, 30));
            task2.setDuration(Duration.ofHours(1));

            manager.addTask(task1);

            assertThrows(IllegalArgumentException.class, () -> manager.addTask(task2));
        }

        @Test
        public void shouldNotThrowWhenTasksDoNotOverlap() {
            FileBackedTaskManager manager = createManager();

            Task task1 = new Task("Task1", "Desc1");
            task1.setStartTime(LocalDateTime.of(2025, 8, 19, 10, 0));
            task1.setDuration(Duration.ofHours(1));

            Task task2 = new Task("Task2", "Desc2");
            task2.setStartTime(LocalDateTime.of(2025, 8, 19, 11, 0));
            task2.setDuration(Duration.ofHours(1));

            assertDoesNotThrow(() -> {
                manager.addTask(task1);
                manager.addTask(task2);
            });
        }
    }
}