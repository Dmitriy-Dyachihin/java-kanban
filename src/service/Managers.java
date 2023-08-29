package service;

public class Managers {

    private Managers() {
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(HistoryManager history) {
        return new InMemoryTaskManager(history);
    }
}