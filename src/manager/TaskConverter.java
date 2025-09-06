package manager;

import task.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskConverter {

    public static String toString(Task task) {
        String epicField = "";
        if (task instanceof Subtask) {
            epicField = String.valueOf(((Subtask) task).getEpicId());
        }

        String start = "";
        String duration = "";
        if (!(task instanceof Epic)) {
            start = task.getStartTime() == null ? "" : task.getStartTime().toString();
            duration = task.getDuration() == null ? "" : String.valueOf(task.getDuration().toMinutes());
        }

        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().name(),
                escape(task.getName()),
                task.getStatus().name(),
                escape(task.getDescription()),
                epicField,
                start,
                duration
        );
    }

    public static Task fromString(String value) {
        String[] f = value.split(",", -1); // -1 → чтобы пустые значения не отбрасывались

        int id = Integer.parseInt(f[0]);
        TaskTypes type = TaskTypes.valueOf(f[1]);
        String name = unescape(f[2]);
        TaskStatus taskStatus = TaskStatus.valueOf(f[3]);
        String description = unescape(f[4]);
        String epicField = f[5];
        String startField = f[6];
        String durationField = f[7];

        LocalDateTime start = startField.isBlank() ? null : LocalDateTime.parse(startField);
        Duration duration = durationField.isBlank() ? null : Duration.ofMinutes(Long.parseLong(durationField));

        switch (type) {
            case TASK -> {
                Task t = new Task(name, description, taskStatus);
                t.setId(id);
                t.setStartTime(start);
                t.setDuration(duration);
                return t;
            }
            case EPIC -> {
                Epic e = new Epic(name, description);
                e.setId(id);
                e.setStatus(taskStatus);
                // duration, startTime и endTime у эпика считаются по подзадачам → не загружаем их
                return e;
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(epicField);
                Subtask s = new Subtask(name, description, epicId, taskStatus);
                s.setId(id);
                s.setStartTime(start);
                s.setDuration(duration);
                return s;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи " + type);
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace(",", "\\,");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        StringBuilder res = new StringBuilder();
        boolean esc = false;
        for (char c : s.toCharArray()) {
            if (esc) {
                res.append(c);
                esc = false;
            } else if (c == '\\') {
                esc = true;
            } else {
                res.append(c);
            }
        }
        return res.toString();
    }
}
