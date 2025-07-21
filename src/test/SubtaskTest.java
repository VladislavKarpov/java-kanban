package test;

import org.junit.jupiter.api.Test;
import task.Status;
import task.Subtask;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    //проверка на создание подзадачи (поля корректно сохраняются и доступны)
    @Test
    void subtaskCreationAndGettersShouldWork() {
        int epicId = 10;
        Subtask subtask = new Subtask("Test Subtask", "Description", epicId);

        assertEquals("Test Subtask", subtask.getName());
        assertEquals("Description", subtask.getDescription());
        assertEquals(epicId, subtask.getEpicId());
        assertEquals(Status.NEW, subtask.getStatus()); // статус по умолчанию
    }

    //проверки на id:
    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask("Subtask1", "Desc1", 1);
        Subtask subtask2 = new Subtask("Subtask2", "Desc2", 1);

        subtask1.setId(100);
        subtask2.setId(100);

        assertEquals(subtask1, subtask2);
        assertEquals(subtask1.hashCode(), subtask2.hashCode());
    }

    //проверка, что toString содержит нужную информацию
    @Test
    void toStringShouldContainFields() {
        Subtask subtask = new Subtask("Subtask", "Desc", 1, Status.NEW);
        subtask.setId(50);

        String str = subtask.toString();
        assertTrue(str.contains("id=50"));
        assertTrue(str.contains("epicId=1"));
        assertTrue(str.contains("Subtask"));
        assertTrue(str.contains("Desc"));
        assertTrue(str.contains("NEW"));
    }


}