package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;


import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    protected abstract T createManager(); // фабричный метод для конкретного менеджера

    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    @Test
    void epicStatusShouldBeNewWhenAllSubtasksNew() {
        Epic epic = manager.addEpic(new Epic("Epic1", "desc"));
        manager.addSubtask(new Subtask("s1", "d", epic.getId(), Status.NEW));
        manager.addSubtask(new Subtask("s2", "d", epic.getId(), Status.NEW));

        assertEquals(Status.NEW, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void epicStatusShouldBeDoneWhenAllSubtasksDone() {
        Epic epic = manager.addEpic(new Epic("Epic2", "desc"));
        manager.addSubtask(new Subtask("s1", "d", epic.getId(), Status.DONE));
        manager.addSubtask(new Subtask("s2", "d", epic.getId(), Status.DONE));

        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void epicStatusShouldBeInProgressWhenMixedNewAndDone() {
        Epic epic = manager.addEpic(new Epic("Epic3", "desc"));
        manager.addSubtask(new Subtask("s1", "d", epic.getId(), Status.NEW));
        manager.addSubtask(new Subtask("s2", "d", epic.getId(), Status.DONE));

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void epicStatusShouldBeInProgressWhenAtLeastOneInProgress() {
        Epic epic = manager.addEpic(new Epic("Epic4", "desc"));
        manager.addSubtask(new Subtask("s1", "d", epic.getId(), Status.IN_PROGRESS));

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void subtaskShouldHaveEpicReference() {
        Epic epic = manager.addEpic(new Epic("Epic5", "desc"));
        Subtask sub = manager.addSubtask(new Subtask("s1", "d", epic.getId(), Status.NEW));

        assertEquals(epic.getId(), sub.getEpicId());
        assertTrue(manager.getEpic(epic.getId()).getSubtaskIds().contains(sub.getId()));
    }

    // ====== Проверка пересечений ======
    @Test
    void shouldThrowExceptionWhenTasksOverlap() {
        Task t1 = new Task("T1", "desc", Status.NEW);
        t1.setStartTime(LocalDateTime.of(2025,1,1,10,0));
        t1.setDuration(Duration.ofMinutes(60));
        manager.addTask(t1);

        Task t2 = new Task("T2", "desc", Status.NEW);
        t2.setStartTime(LocalDateTime.of(2025,1,1,10,30)); // пересекается
        t2.setDuration(Duration.ofMinutes(30));

        assertThrows(IllegalArgumentException.class, () -> manager.addTask(t2));
    }

    @Test
    void shouldNotThrowWhenTasksDoNotOverlap() {
        Task t1 = new Task("T1", "desc", Status.NEW);
        t1.setStartTime(LocalDateTime.of(2025,1,1,10,0));
        t1.setDuration(Duration.ofMinutes(30));
        manager.addTask(t1);

        Task t2 = new Task("T2", "desc", Status.NEW);
        t2.setStartTime(LocalDateTime.of(2025,1,1,11,0)); // не пересекается
        t2.setDuration(Duration.ofMinutes(30));

        assertDoesNotThrow(() -> manager.addTask(t2));
    }
}
