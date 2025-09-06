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
    public void setUp() {
        manager = createManager();
    }

    @Test
    public void testAddAndGetTask() {
        Task task = new Task("Task", "Desc");
        manager.addTask(task);

        Task retrieved = manager.getTask(task.getId());
        assertNotNull(retrieved);
        assertEquals(task, retrieved);
    }

    @Test
    public void testEpicWithSubtasksStatusNew() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask s1 = new Subtask("Sub1", "Desc", epic.getId());
        Subtask s2 = new Subtask("Sub2", "Desc", epic.getId());
        manager.addSubtask(s1);
        manager.addSubtask(s2);

        assertEquals(TaskStatus.NEW, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void testEpicWithAllSubtasksDone() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask s1 = new Subtask("Sub1", "Desc", epic.getId(), TaskStatus.DONE);
        Subtask s2 = new Subtask("Sub2", "Desc", epic.getId(), TaskStatus.DONE);
        manager.addSubtask(s1);
        manager.addSubtask(s2);

        assertEquals(TaskStatus.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void testEpicWithMixedSubtasksNewAndDone() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask s1 = new Subtask("Sub1", "Desc", epic.getId(), TaskStatus.NEW);
        Subtask s2 = new Subtask("Sub2", "Desc", epic.getId(), TaskStatus.DONE);
        manager.addSubtask(s1);
        manager.addSubtask(s2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void testEpicWithSubtasksInProgress() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask s1 = new Subtask("Sub1", "Desc", epic.getId(), TaskStatus.IN_PROGRESS);
        Subtask s2 = new Subtask("Sub2", "Desc", epic.getId(), TaskStatus.IN_PROGRESS);
        manager.addSubtask(s1);
        manager.addSubtask(s2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void subtaskShouldHaveEpicReference() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask sub = new Subtask("Sub", "Desc", epic.getId());
        manager.addSubtask(sub);

        assertEquals(epic.getId(), sub.getEpicId());
    }

    @Test
    public void shouldDetectOverlap() {
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
    public void historyShouldHandleDuplicatesCorrectly() {
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
    public void historyShouldBeEmptyInitially() {
        assertTrue(manager.getHistory().isEmpty());
    }

}