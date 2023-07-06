package service;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubTasks();

    ArrayList<Epic> getEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    ArrayList<Subtask> getTasksByEpic(Epic epic);

    void updateStatus(Epic epic);

}