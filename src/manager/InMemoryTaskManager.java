package manager;

import task.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    int nextId = 1;
    //оставил мапы protected
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    //изменил название метода
    private final Comparator<Task> taskStartTimeThenIdComparator = Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparingInt(Task::getId);

    //изменил название метода
    private final NavigableSet<Task> sortedByPriorityTasks = new TreeSet<>(taskStartTimeThenIdComparator);

    //оставил protected тут для использования в FileBackedTaskManager
    protected void addToPrioritized(Task task) {
        if (task.getType() == TaskTypes.EPIC) return;
        if (task.getStartTime() != null && task.getDuration() != null) {
            sortedByPriorityTasks.add(task);
        }

    }

    private void removeFromPrioritized(int id) {
        sortedByPriorityTasks.removeIf(task -> task.getId() == id);
    }

    private void updateInPrioritized(Task task) {
        removeFromPrioritized(task.getId());
        addToPrioritized(task);
    }

    private boolean isOverlapping(Task taskA, Task taskB) {
        if (taskA.getStartTime() == null || taskA.getDuration() == null) return false;
        if (taskB.getStartTime() == null || taskB.getDuration() == null) return false;

        LocalDateTime startTaskA = taskA.getStartTime();
        LocalDateTime endTaskA = taskA.getEndTime();
        LocalDateTime startTaskB = taskB.getStartTime();
        LocalDateTime endTaskB = taskB.getEndTime();

        //заменил + также переписал названия переменных
        return ((startTaskA.isEqual(startTaskB) || startTaskA.isBefore(startTaskB)) && endTaskA.isAfter(startTaskB))
                || ((startTaskB.isEqual(startTaskA) || startTaskB.isBefore(startTaskA)) && endTaskB.isAfter(startTaskA));
    }

    //переименовал метод
    private void validateNoOverlap(Task candidate) {
        if (candidate.getStartTime() == null || candidate.getDuration() == null) return;

        boolean hasOverlap = sortedByPriorityTasks.stream()
                .filter(t -> t.getId() != candidate.getId())
                .anyMatch(t -> isOverlapping(candidate, t));
        if (hasOverlap) {
            throw new IllegalArgumentException("Задачи пересекаются по времени: " + candidate);
        }
    }

    //также еще тут оставил protected
    protected void updateEpicStatus(Epic epic) {
        List<Subtask> subtaskList = getSubtasksOfEpic(epic.getId());
        if (subtaskList.isEmpty()) {
            epic.setStatus(Status.NEW);
            epic.updateTimeFields(subtasks);
            return;
        }

        boolean allNew = subtaskList.stream()
                .allMatch(sTask -> sTask.getStatus() == Status.NEW);
        boolean allDone = subtaskList.stream()
                .allMatch(sTask -> sTask.getStatus() == Status.DONE);

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        epic.updateTimeFields(subtasks);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(this::removeFromPrioritized);
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(this::removeFromPrioritized);
        epics.clear();
        subtasks.clear();
    }


    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(this::removeFromPrioritized);
        epics.values().forEach(Epic::clearSubtasks);
        epics.values().forEach(this::updateEpicStatus);
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Task addTask(Task task) {
        validateNoOverlap(task);
        task.setId(nextId++);
        addToPrioritized(task);
        return tasks.put(task.getId(), task);
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(nextId++);
        updateEpicStatus(epic);
        return epics.put(epic.getId(), epic);
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (subtask.getId() == subtask.getEpicId()) {
            throw new IllegalArgumentException("Subtask не может быть своим эпиком");

        }
        validateNoOverlap(subtask);
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic);
        }
        addToPrioritized(subtask);
        return subtasks.get(subtask.getId());
    }

    @Override
    public Task updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) return null;
        validateNoOverlap(task);
        tasks.put(task.getId(), task);
        updateInPrioritized(task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) return null;
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) return null;
        validateNoOverlap(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateInPrioritized(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        removeFromPrioritized(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            epic.getSubtaskIds().forEach(this::deleteSubtask); // удалит и из prioritized
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
            }

        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return List.of();
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedByPriorityTasks);
    }

}
