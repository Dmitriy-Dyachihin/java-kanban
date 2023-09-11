package http;

import service.HistoryManager;
import service.Managers;
import service.TaskManager;
import service.TaskManagerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskStatus.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest<T extends TaskManagerTest<HttpTaskManager>> {
    private KVServer server;
    private TaskManager manager;

    @BeforeEach
    public void createManager() {
        try {
            server = new KVServer();
            server.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            manager = Managers.getDefault(historyManager);
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при создании менеджера");
        }
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldLoadTasks() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, Instant.now(), 0);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW, Instant.now(), 0);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getTasks(), list);
    }

    @Test
    public void shouldLoadEpics() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, Instant.now(),0);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", Status.NEW, Instant.now(),0);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getEpics(), list);
    }

    @Test
    public void shouldLoadSubtasks() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, Instant.now(),0);
        Subtask subtask1 = new Subtask("Подзадача 1 ", "Описание подзадачи 1", Status.NEW,
                epic.getId(), Instant.now(),2);
        Subtask subtask2 = new Subtask("Подзадача 2 ", "Описание подзадачи 2", Status.NEW,
                epic.getId(), Instant.now(),2);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getSubtasks(), list);
    }

}