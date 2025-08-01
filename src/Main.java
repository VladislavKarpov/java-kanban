import manager.TaskManager;
import manager.InMemoryTaskManager;
import task.*;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Task 1", "Desc 1");
        Task task2 = new Task("Task 2", "Desc 2");
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epicWithSubs = new Epic("Epic With Subtasks", "Epic desc");
        manager.addEpic(epicWithSubs);
        int epicId = epicWithSubs.getId();

        Subtask sub1 = new Subtask("Subtask 1", "Subdesc 1", epicId);
        Subtask sub2 = new Subtask("Subtask 2", "Subdesc 2", epicId);
        Subtask sub3 = new Subtask("Subtask 3", "Subdesc 3", epicId);
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);
        manager.addSubtask(sub3);

        Epic epicWithoutSubs = new Epic("Epic Without Subtasks", "Empty epic desc");
        manager.addEpic(epicWithoutSubs);


        manager.getTask(task1.getId());
        manager.getEpic(epicId);
        manager.getSubtask(sub2.getId());
        manager.getTask(task2.getId());
        manager.getSubtask(sub1.getId());
        manager.getEpic(epicWithoutSubs.getId());
        manager.getSubtask(sub3.getId());
        manager.getTask(task1.getId());  // Повторный запрос
        manager.getEpic(epicId);

        printHistory(manager);

        System.out.println("\nУдаляем задачу Task 1");
        manager.deleteTask(task1.getId());
        printHistory(manager);


        System.out.println("\nУдаляем эпик с тремя подзадачами (Epic With Subtasks)");
        manager.deleteEpic(epicId);
        printHistory(manager);

    }

    private static void printHistory(TaskManager manager) {
        System.out.println("История просмотров:");
        for (Task t : manager.getHistory()) {
            System.out.println(t);
        }
    }
}

