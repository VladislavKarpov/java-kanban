package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Epic extends Task {

    private final List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;


    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.setDuration(Duration.ZERO);
        this.setStartTime(null);
        this.endTime = null;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtaskId(int id) {
        subtaskIds.remove((Integer) id);
    }

    public void clearSubtasks() {
        subtaskIds.clear();
    }

    public void updateTimeFields(Map<Integer, Subtask> subtasks) {
        if (subtaskIds.isEmpty()) {
            this.setDuration(Duration.ZERO);
            this.setStartTime(null);
            this.endTime = null;
            return;
        }

        Duration total = Duration.ZERO;
        LocalDateTime earliest = null;
        LocalDateTime latest = null;

        for (Integer subtaskId : subtaskIds) {
            Subtask sTask = subtasks.get(subtaskId);
            if (sTask.getStartTime() == null || sTask.getDuration() == null) continue;

            total = total.plus(sTask.getDuration());

            LocalDateTime sStart = sTask.getStartTime();
            LocalDateTime sEnd = sTask.getEndTime();

            if (earliest == null || sStart.isBefore(earliest)) {
                earliest = sStart;
            }
            if (latest == null || sEnd.isAfter(latest)) {
                latest = sEnd;
            }
        }

        this.setDuration(total);
        this.setStartTime(earliest);
        this.endTime = latest;
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.EPIC;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + endTime +
                ", subtasks=" + subtaskIds +
                '}';
    }
}