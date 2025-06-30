package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public List<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void addSubtaskId(int id) {
        subtaskId.add(id);
    }

    public void removeSubtaskId(int id) {
        subtaskId.remove((Integer) id);
    }

    public void clearSubtasks() {
        subtaskId.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtaskId +
                '}';
    }
}