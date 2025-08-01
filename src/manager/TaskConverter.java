package manager;

import task.*;

public class TaskConverter {

    public static String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        String type;

        if (task instanceof Epic) {
            type = "EPIC";
            sb.append(String.format("%d,%s,%s,%s,%s", task.getId(), type, task.getName(),
                    task.getStatus(),task.getDescription()));

        } else if (task instanceof Subtask) {
            type = "SUBTASK";
            int epicId = ((Subtask) task).getEpicId();
            sb.append(String.format("%d,%s,%s,%s,%s,%d",task.getId(), type, task.getName(),
                    task.getStatus(), task.getDescription(), epicId));

        } else {
            type = "TASK";
            sb.append(String.format("%d,%s,%s,%s,%s", task.getId(), type, task.getName(),
                    task.getStatus(), task.getDescription()));

        }
        return sb.toString();
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        TaskTypes type = TaskTypes.valueOf(fields[1]);
        String name  = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        return switch (type) {
            case TASK -> {
                Task task = new Task(name, description, status);
                task.setId(id);
                yield task;
            }
            case EPIC -> {
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                yield epic;
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(name, description, epicId, status);
                subtask.setId(id);
                yield subtask;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи " + type);

        };

    }
}
