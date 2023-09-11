package http;

import com.google.gson.*;
import service.FileBackedTasksManager;
import service.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Instant;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    final static String TASKS = "tasks";
    final static String SUBTASKS = "subtasks";
    final static String EPICS = "epics";
    final static String HISTORY = "history";
    final KVTaskClient client;
    private static final Gson gson =
            new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();

    public HttpTaskManager(HistoryManager historyManager, String path) throws IOException, InterruptedException {
        super(historyManager);
        client = new KVTaskClient(path);

        JsonElement jsonTasks = JsonParser.parseString(client.load(TASKS));
        if (!jsonTasks.isJsonNull()) {
            JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonTasksArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                this.addTask(task);
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load(EPICS));
        if (!jsonEpics.isJsonNull()) {
            JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                Epic task = gson.fromJson(jsonEpic, Epic.class);
                this.addEpic(task);
            }
        }

        JsonElement jsonSubtasks = JsonParser.parseString(client.load(SUBTASKS));
        if (!jsonSubtasks.isJsonNull()) {
            JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubtasksArray) {
                Subtask task = gson.fromJson(jsonSubtask, Subtask.class);
                this.addSubtask(task);
            }
        }

        JsonElement jsonHistoryList = JsonParser.parseString(client.load(HISTORY));
        if (!jsonHistoryList.isJsonNull()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                int taskId = jsonTaskId.getAsInt();
                if (this.subtasks.containsKey(taskId)) {
                    this.getSubtaskById(taskId);
                } else if (this.epics.containsKey(taskId)) {
                    this.getEpicById(taskId);
                } else if (this.tasks.containsKey(taskId)) {
                    this.getTaskById(taskId);
                }
            }
        }
    }

    @Override
    public void save() {
        client.put(TASKS, gson.toJson(tasks.values()));
        client.put(SUBTASKS, gson.toJson(subtasks.values()));
        client.put(EPICS, gson.toJson(epics.values()));
        client.put(HISTORY, gson.toJson(this.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }
}