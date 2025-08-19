package manager;

import task.Task;

import java.util.List;
import java.util.stream.Collectors;

public class HistoryConverter {
    public static String toString(HistoryManager historyManager) {
        List<Task> history = historyManager.getHistory();
        return history.stream()
                .map(task -> String.valueOf(task.getId()))
                .collect(Collectors.joining(","));
    }

    public static List<Integer> fromString(String value) {
        if (value == null || value.isBlank()) return List.of();
        String[] parts = value.split(",");
        return List.of(parts).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
