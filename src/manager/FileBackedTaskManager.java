package manager;

import exceptions.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    public static void main(String[] args) {
        File file = new File("tasks.csv");

        FileBackedTaskManager originalManager = new FileBackedTaskManager(file);

        Task task1 = new Task("Обучение", "Сделать задачу 7 спринта");
        Task task2 = new Task("Отдых", "Поехать на отдых");
        originalManager.addTask(task1);
        originalManager.addTask(task2);

        Epic epic = new Epic("Подготовка к отдыху", "Список необходимых вещей");
        originalManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Купить палатку", "Почитать отзывы на возон", epic.getId());
        Subtask subtask2 = new Subtask("Собрать походный инвентарь", "Котелок", epic.getId());
        originalManager.addSubtask(subtask1);
        originalManager.addSubtask(subtask2);


        originalManager.getTask(task1.getId());
        originalManager.getEpic(epic.getId());
        originalManager.getSubtask(subtask2.getId());


        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        System.out.println("\n---- Загруженные задачи ----");
        for (Task task : loadedManager.getAllTasks()) {
            System.out.println(task);

        }

        System.out.println("\n---- Загруженные эпики ----");
        for (Epic e : loadedManager.getAllEpics()) {
            System.out.println(e);

        }

        System.out.println("\n---- Загруженные подзадачи ----");
        for (Subtask s : loadedManager.getAllSubtasks()) {
            System.out.println(s);

        }

        System.out.println("\n---- История просмотров ----");
        for (Task t : loadedManager.getHistory()) {
            System.out.println(t);

        }
    }

    private final File file;
    private FileBackedTaskManager manager;

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
                Task task = TaskConverter.fromString(taskLines[i]);
                int id = task.getId();
                if (task instanceof Epic) {
                    manager.epics.put(id, (Epic) task);

                } else if (task instanceof Subtask) {
                    manager.subtasks.put(id, (Subtask) task);

                } else {
                    manager.tasks.put(id, task);

                }
                manager.setIdIToTask(task.getId());

            }

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
        Task added = super.addTask(task);
        save();
        return added;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic added = super.addEpic(epic);
        save();
        return added;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask added = super.addSubtask(subtask);
        save();
        return added;
    }

    @Override
    public Task updateTask(Task task) {
        Task updated = super.updateTask(task);
        save();
        return updated;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updated = super.updateEpic(epic);
        save();
        return updated;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updated = super.updateSubtask(subtask);
        save();
        return updated;
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
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }


}
