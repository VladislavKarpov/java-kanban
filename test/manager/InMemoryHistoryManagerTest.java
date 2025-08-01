package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setup() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddTasksToHistoryInCorrectOrder() {
        Task task1 = new Task("Task 1", "Desc 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Desc 2");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void shouldNotAddDuplicatesAndMoveTaskToEnd() {
        Task task1 = new Task("Task 1", "Desc");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Desc");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1); // повторный просмотр task1

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1)); // task1 должен быть в конце
    }

    @Test
    void shouldRemoveTaskFromHistoryById() {
        Task task1 = new Task("Task 1", "Desc");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Desc");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertFalse(history.contains(task1));
    }

    @Test
    void shouldHandleEmptyHistoryGracefully() {
        assertTrue(historyManager.getHistory().isEmpty());

        // удаление из пустой истории не вызывает исключений
        assertDoesNotThrow(() -> historyManager.remove(42));
    }
}