package test;

import manager.*;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void managersShouldReturnInitializedInstances() {
        TaskManager manager = Managers.getDefault();
        HistoryManager history = Managers.getDefaultHistory();

        assertNotNull(manager);
        assertNotNull(history);
    }
    //Managers возвращает проинициализированные объекты

}