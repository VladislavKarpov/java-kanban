import manager.Managers;
import manager.TaskManager;
import manager.InMemoryTaskManager;
import task.*;

import java.util.List;

public class Main {
        public static void main(String[] args) {
            TaskManager manager = Managers.getDefault(); // реализация с утилитарным классом

            System.out.println("Добавим обычную задачу:");
            Task task = new Task("Переезд", "Собрать вещи, вызвать грузовое такси и перевезти всё");
            manager.addTask(task);
            System.out.println(manager.getTask(task.getId()));

            System.out.println("\nДобавим 'эпик':");
            Epic epic = new Epic("Организация праздника", "Подготовить день рождения бабушки");
            manager.addEpic(epic);
            System.out.println(manager.getEpic(epic.getId()));

            System.out.println("\nДобавим подзадачи к 'эпику':");
            Subtask sub1 = new Subtask("Купить торт", "Шоколадный", epic.getId());
            Subtask sub2 = new Subtask("Пригласить гостей", "Позвонить друзьям", epic.getId());
            manager.addSubtask(sub1);
            manager.addSubtask(sub2);

            for (Subtask sub : manager.getSubtasksOfEpic(epic.getId())) {
                System.out.println(sub);
            }

            System.out.println("\nОбновим статус первой подзадачи:");
            sub1.setStatus(Status.DONE);
            manager.updateSubtask(sub1);
            System.out.println(manager.getEpic(epic.getId()));

            System.out.println("\nОбновим статус второй подзадачи:");
            sub2.setStatus(Status.DONE);
            manager.updateSubtask(sub2);
            System.out.println(manager.getEpic(epic.getId()));

            //печать истории просмотров
            List<Task> history = manager.getHistory();
            for (Task t : history) {
                System.out.println(t);
            }
        }


}

