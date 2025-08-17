package manager;

import exceptions.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,description,epic\n");

            for (Task task : tasks.values()) {
                writer.write(TaskConverter.toString(task) + "\n");

            }

            for (Epic epic : epics.values()) {
                writer.write(TaskConverter.toString(epic) + "\n");

            }

            for (Subtask subtask : subtasks.values()) {
                writer.write(TaskConverter.toString(subtask) + "\n");

            }

            writer.write("\n");
            writer.write(HistoryConverter.toString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл", e);

        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            String content = Files.readString(file.toPath());
            String[] parts = content.split("\n\n");
            String[] taskLines = parts[0].split("\n");

            for (int i = 1; i < taskLines.length; i++) {
                if (taskLines[i].isBlank()) continue;
                Task task = TaskConverter.fromString(taskLines[i]);
                manager.setIdIToTask(task.getId());
                switch (task.getType()) {
                    case TASK -> manager.addTask(task);
                    case EPIC -> manager.addEpic((Epic) task);
                    case SUBTASK -> {
                        manager.subtasks.put(task.getId(), (Subtask) task);
                        Epic ep = manager.epics.get(((Subtask) task).getEpicId());
                        if (ep != null) ep.addSubtaskId(task.getId());

                    }
                }
            }

            manager.epics.values().forEach(manager::updateEpicStatus);
            manager.tasks.values().forEach(manager::addToPrioritized);
            manager.subtasks.values().forEach(manager::addToPrioritized);


            if (parts.length > 1) {
                List<Integer> history = HistoryConverter.fromString(parts[1]);
                for (Integer taskId : history) {
                    manager.getTaskById(taskId);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки данных из файла", e);
        }
        return manager;
    }

    private void setIdIToTask(int id) {
        if (id > this.nextId) {
            this.nextId = id;
        }
    }

    private Task getTaskById(int id) {
        if (tasks.containsKey(id)) return getTask(id);
        if (epics.containsKey(id)) return getEpic(id);
        return subtasks.get(id);
    }

    @Override
    public Task addTask(Task task) {
        Task t = super.addTask(task);
        save();
        return t;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic e = super.addEpic(epic);
        save();
        return e;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask s = super.addSubtask(subtask);
        save();
        return s;
    }

    @Override
    public Task updateTask(Task task) {
        Task t = super.updateTask(task);
        save();
        return t;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic e = super.updateEpic(epic);
        save();
        return e;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask s = super.updateSubtask(subtask);
        save();
        return s;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task t = super.getTask(id);
        save();
        return t;
    }

    @Override
    public Epic getEpic(int id) {
        Epic e = super.getEpic(id);
        save();
        return e;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask s = super.getSubtask(id);
        save();
        return s;
    }

}
