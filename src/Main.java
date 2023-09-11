import http.InstantAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;
import http.KVServer;
import taskStatus.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.time.Instant;

public class Main {

    public static void main(String[] args) {

        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();

            KVServer server = new KVServer();
            server.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            TaskManager httpTaskManager = Managers.getDefault(historyManager);

            Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW, Instant.now(), 1);
            httpTaskManager.createTask(task);

            Epic epic = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, Instant.now(),0);
            httpTaskManager.createEpic(epic);

            Subtask subtask = new Subtask("Подзадача 1 ", "Описание подзадачи 1", Status.NEW,
                    epic.getId(), Instant.now(),2);
            httpTaskManager.createSubtask(subtask);

            httpTaskManager.getTaskById(task.getId());
            httpTaskManager.getEpicById(epic.getId());
            httpTaskManager.getSubtaskById(subtask.getId());

            System.out.println(gson.toJson(httpTaskManager.getTasks()));
            System.out.println(gson.toJson(httpTaskManager.getEpics()));
            System.out.println(gson.toJson(httpTaskManager.getSubtasks()));
            System.out.println(httpTaskManager);

            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}