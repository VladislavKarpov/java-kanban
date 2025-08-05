import manager.FileBackedTaskManager;
import manager.TaskManager;
import task.*;

import java.io.File;

public class Main {

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

    private static void printHistory(TaskManager manager) {
        System.out.println("История просмотров:");
        for (Task t : manager.getHistory()) {
            System.out.println(t);

        }
    }
}

