package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;


    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.duration = Duration.ZERO;
        this.startTime = null;
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

    public void updateTimeFields(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            this.duration = Duration.ZERO;
            this.startTime = null;
            this.endTime = null;
            return;
        }

        Duration total = Duration.ZERO;
        LocalDateTime earliest = null;
        LocalDateTime latest = null;

        for (Subtask sTask : subtasks) {
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

        this.duration = total;
        this.startTime = earliest;
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
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                ", subtasks=" + subtaskIds +
                '}';
    }
}