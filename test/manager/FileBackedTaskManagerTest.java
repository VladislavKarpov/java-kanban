package manager;

import java.io.File;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(new File("test_tasks.csv"));
    }
}