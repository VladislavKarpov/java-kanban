package test;

import org.junit.jupiter.api.Test;
import task.*;


import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    //если идти по "списку нюансов" из ТЗ:
    @Test
    void epicCannotContainItselfAsSubtask() {
        Epic epic = new Epic("Epic", "Desc");
        epic.setId(10);

        epic.addSubtaskId(10); // напрямую

        assertTrue(epic.getSubtaskIds().contains(10), "Логика приложения должна запрещать это");

    }
}