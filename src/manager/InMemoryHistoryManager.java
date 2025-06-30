package manager;

import task.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final LinkedList<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        history.add(task);
        if (history.size() > 10) {
            history.removeFirst();// удаляем самый последний просмотр
        }
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(history);
    }


}
