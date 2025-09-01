package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    public void subtaskCreationAndGettersShouldWork() {
        int epicId = 10;
        Subtask subtask = new Subtask("Test Subtask", "Description", epicId);

        assertEquals("Test Subtask", subtask.getName());
        assertEquals("Description", subtask.getDescription());
        assertEquals(epicId, subtask.getEpicId());
        assertEquals(TaskStatus.NEW, subtask.getStatus()); // статус по умолчанию
    }

    @Test
    public void subtasksWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask("Subtask1", "Desc1", 1);
        Subtask subtask2 = new Subtask("Subtask2", "Desc2", 1);

        subtask1.setId(100);
        subtask2.setId(100);

        assertEquals(subtask1, subtask2);
        assertEquals(subtask1.hashCode(), subtask2.hashCode());
    }

    @Test
    public void toStringShouldContainFields() {
        Subtask subtask = new Subtask("Subtask", "Desc", 1, TaskStatus.NEW);
        subtask.setId(50);

        String str = subtask.toString();
        assertTrue(str.contains("id=50"));
        assertTrue(str.contains("epicId=1"));
        assertTrue(str.contains("Subtask"));
        assertTrue(str.contains("Desc"));
        assertTrue(str.contains("NEW"));
    }


}