package manager;

import task.Task;
import task.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTwoTest {
    private HistoryManager history;
    private Task t1, t2, t3;

    @BeforeEach
    void setUp() {
        history = new InMemoryHistoryManager();
        t1 = new Task("T1", "desc", Status.NEW);
        t1.setId(1);
        t2 = new Task("T2", "desc", Status.NEW);
        t2.setId(2);
        t3 = new Task("T3", "desc", Status.NEW);
        t3.setId(3);
    }

    @Test
    void shouldReturnEmptyWhenHistoryIsEmpty() {
        assertTrue(history.getHistory().isEmpty());
    }

}
