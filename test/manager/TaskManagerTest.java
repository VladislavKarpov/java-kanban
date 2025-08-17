package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    protected abstract T createManager();

    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    @Test
    void testAddAndGetTask() {
        Task task = new Task("Task", "Desc");
        manager.addTask(task);

        Task retrieved = manager.getTask(task.getId());
        assertNotNull(retrieved);
        assertEquals(task, retrieved);
    }

    @Test
    void testEpicWithSubtasksStatusNew() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask s1 = new Subtask("Sub1", "Desc", epic.getId());
        Subtask s2 = new Subtask("Sub2", "Desc", epic.getId());
        manager.addSubtask(s1);
        manager.addSubtask(s2);

        assertEquals(Status.NEW, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void testEpicWithAllSubtasksDone() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask s1 = new Subtask("Sub1", "Desc", epic.getId(), Status.DONE);
        Subtask s2 = new Subtask("Sub2", "Desc", epic.getId(), Status.DONE);
        manager.addSubtask(s1);
        manager.addSubtask(s2);

        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void testEpicWithMixedSubtasksNewAndDone() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask s1 = new Subtask("Sub1", "Desc", epic.getId(), Status.NEW);
        Subtask s2 = new Subtask("Sub2", "Desc", epic.getId(), Status.DONE);
        manager.addSubtask(s1);
        manager.addSubtask(s2);

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void testEpicWithSubtasksInProgress() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask s1 = new Subtask("Sub1", "Desc", epic.getId(), Status.IN_PROGRESS);
        Subtask s2 = new Subtask("Sub2", "Desc", epic.getId(), Status.IN_PROGRESS);
        manager.addSubtask(s1);
        manager.addSubtask(s2);

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void subtaskShouldHaveEpicReference() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask sub = new Subtask("Sub", "Desc", epic.getId());
        manager.addSubtask(sub);

        assertEquals(epic.getId(), sub.getEpicId());
    }

    @Test
    void shouldDetectOverlap() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask s1 = new Subtask("Sub1", "Desc", epic.getId());
        s1.setStartTime(LocalDateTime.of(2025, 8, 17, 10, 0));
        s1.setDuration(Duration.ofHours(1));
        manager.addSubtask(s1);

        Subtask s2 = new Subtask("Sub2", "Desc", epic.getId());
        s2.setStartTime(LocalDateTime.of(2025, 8, 17, 10, 30));
        s2.setDuration(Duration.ofHours(1));

        assertThrows(IllegalArgumentException.class, () -> manager.addSubtask(s2),
                "Добавление пересекающейся задачи должно выбрасывать исключение");
    }

    @Test
    void historyShouldHandleDuplicatesCorrectly() {
        Task t1 = new Task("T1", "Desc");
        Task t2 = new Task("T2", "Desc");
        manager.addTask(t1);
        manager.addTask(t2);

        manager.getTask(t1.getId());
        manager.getTask(t2.getId());
        manager.getTask(t1.getId()); // повторный просмотр

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
        assertEquals(t2, history.get(0));
        assertEquals(t1, history.get(1));
    }

    @Test
    void historyShouldBeEmptyInitially() {
        assertTrue(manager.getHistory().isEmpty());
    }

}