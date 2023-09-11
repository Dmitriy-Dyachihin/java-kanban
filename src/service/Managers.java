package service;

import http.HttpTaskManager;
import http.KVServer;

import java.io.IOException;

public class Managers {

    private Managers() {
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultTaskManager(HistoryManager history) {
        return new InMemoryTaskManager(history);
    }

    public static HttpTaskManager getDefault(HistoryManager historyManager) throws IOException, InterruptedException {
        return new HttpTaskManager(historyManager, "http://localhost:" + KVServer.PORT);
    }
}
