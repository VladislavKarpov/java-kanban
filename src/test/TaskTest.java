package test;

import org.junit.jupiter.api.Test;
import task.*;


import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void testStatusSetAndGet() {
        Task task = new Task("Task", "Desc", Status.IN_PROGRESS);
        assertEquals(Status.NEW, task.getStatus(), "Статус игнорируется и устанавливается NEW");

        task.setStatus(Status.DONE);
        assertEquals(Status.DONE, task.getStatus());
    }


    //если идти по "списку нюансов" из ТЗ:

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("A", "B");
        Task task2 = new Task("C", "D");
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    void epicAndSubtaskWithSameIdShouldBeEqualToSameType() {
        Epic epic1 = new Epic("Epic1", "Description");
        Epic epic2 = new Epic("Epic2", "OtherDesc");
        epic1.setId(42);
        epic2.setId(42);

        assertEquals(epic1, epic2);

        Subtask sub1 = new Subtask("Sub", "A", 1);
        Subtask sub2 = new Subtask("Another", "B", 1);
        sub1.setId(99);
        sub2.setId(99);

        assertEquals(sub1, sub2);
    }
}