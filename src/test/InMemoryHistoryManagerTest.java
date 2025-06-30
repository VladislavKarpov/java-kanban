package test;

import manager.*;
import task.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    //HistoryManager сохраняет "origin" данные задачи
    @Test
    void historyManagerShouldRetainOriginalTaskState() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task = new Task("Test", "Before");
        task.setId(1);
        historyManager.add(task);

        task.setName("Changed");
        task.setDescription("After");

        Task fromHistory = historyManager.getHistory().get(0);

        assertEquals("Changed", fromHistory.getName());
    }


}