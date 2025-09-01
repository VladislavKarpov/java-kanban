package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.TaskStatus;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    public void setup() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldReturnEmptyWhenHistoryIsEmpty() {
        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой");
    }

    @Test
    public void shouldAddTasksToHistoryInCorrectOrder() {
        Task task1 = new Task("Task 1", "Desc 1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Desc 2", TaskStatus.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    public void shouldNotAddDuplicatesAndMoveTaskToEnd() {
        Task task1 = new Task("Task 1", "Desc 1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Desc 2", TaskStatus.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1); // повторное добавление

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История не должна содержать дубликаты");
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1), "Повторно добавленная задача должна быть в конце");
    }

    @Test
    public void shouldRemoveTaskFromHistoryById() {
        Task task1 = new Task("Task 1", "Desc 1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Desc 2", TaskStatus.NEW);
        task2.setId(2);
        Task task3 = new Task("Task 3", "Desc 3", TaskStatus.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1);
        List<Task> history1 = historyManager.getHistory();
        assertEquals(2, history1.size());
        assertFalse(history1.contains(task1));

        historyManager.remove(2);
        List<Task> history2 = historyManager.getHistory();
        assertEquals(1, history2.size());
        assertFalse(history2.contains(task2));

        historyManager.remove(3);
        List<Task> history3 = historyManager.getHistory();
        assertTrue(history3.isEmpty());
    }

    @Test
    public void removingNonexistentTaskShouldNotThrow() {
        assertDoesNotThrow(() -> historyManager.remove(42),
                "Удаление несуществующей задачи не должно бросать исключение");
    }
}