package service;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    public static List<Task> history = new ArrayList<>(); // Список для хранения 10 последних задач

    @Override
    public void add(Task task) {
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        for(int i = 0 ; i < history.size() ; i++) {
            if (history.size() > 10) {
                history.remove(0);
            }
        }
        return history;
    }
}