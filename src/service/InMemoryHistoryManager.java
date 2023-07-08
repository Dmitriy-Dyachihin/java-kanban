package service;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    public static final int MAX_SIZE_OF_HISTORY = 10;

    public static List<Task> history = new ArrayList<>(); // Список для хранения 10 последних задач

    @Override
    public void add(Task task) {
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        for(int i = 0 ; i < history.size() ; i++) {
            if (history.size() > MAX_SIZE_OF_HISTORY) {
                history.remove(0);
            }
        }
        return history;
    }
}