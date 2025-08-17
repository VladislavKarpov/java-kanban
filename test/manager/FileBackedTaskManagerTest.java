package manager;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(new File("test_tasks.csv"));
    }

    // Дополнительно: проверка работы с файлом
    @org.junit.jupiter.api.Test
    void shouldThrowExceptionWhenFileCorrupted() {
        FileBackedTaskManager manager = new FileBackedTaskManager(new File("/root/!invalid"));
        assertThrows(RuntimeException.class, manager::getAllTasks);
    }
}