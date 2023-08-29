package service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskStatus.Status;
import tasks.Epic;
import tasks.Task;

import java.io.File;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    File file = new File("source/results.csv");
    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
    }

    @Test
    public void shouldCorrectlySaveAndLoad() {
        Task task = new Task("Задача 1", "Описание 1", Status.NEW, Instant.now(), 0);
        manager.createTask(task);
        Epic epic = new Epic("Эпик 1", "Описание эпик 1", Status.NEW, Instant.now(), 0);
        manager.createEpic(epic);
        FileBackedTasksManager newManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        newManager.loadFromFile(file);
        assertEquals(List.of(task), manager.getTasks());
        assertEquals(List.of(epic), manager.getEpics());
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        fileManager.save();
        fileManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, manager.getTasks());
        assertEquals(Collections.EMPTY_LIST, manager.getEpics());
        assertEquals(Collections.EMPTY_LIST, manager.getSubtasks());
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        fileManager.save();
        fileManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

}