package service;

import taskStatus.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements  TaskManager {

    private static int id = 0;

    private final Map<Integer, Task> tasks = new HashMap<>(); // Список для хранения задач
    private final Map<Integer, Epic> epics = new HashMap<>(); // Список для хранения эпиков
    private final Map<Integer, Subtask> subtasks = new HashMap<>(); // Список для хранения подзадач

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public int assignId() {
        return ++id;
    }
    @Override
    public Task createTask(Task task) {
        if(task == null) {
            System.out.println("Некорректный ввод");
            return null;
        }
        int idOfNewTask = assignId();
        task.setId(idOfNewTask);
        tasks.put(idOfNewTask, task);
        return task;

    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if(subtask == null) {
            System.out.println("Некорректный ввод");
            return null;
        } else {
            int idOfNewSubtask = assignId();
            subtask.setId(idOfNewSubtask);
            Epic epic = epics.get(subtask.getEpicId());
            if(epic != null) {
                subtasks.put(idOfNewSubtask, subtask);
                epic.setSubtasksIds(idOfNewSubtask);
                updateStatus(epic);
                return subtask;
            } else {
                System.out.println("Не существует заданного эпика");
                return null;
            }
        }
    }

    @Override
    public Epic createEpic(Epic epic) {
        if(epic == null) {
            System.out.println("Некорректный ввод");
            return null;
        } else {
            int idOfNewEpic = assignId();
            epic.setId(idOfNewEpic);
            epics.put(idOfNewEpic, epic);
            updateStatus(epic);
            return epic;
        }
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for(Epic epic : epics.values()) {
            epic.getSubtasksIds().clear();
            updateStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        if(tasks.containsKey(task.getId()) && task != null) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Некорректный ввод");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if(subtasks.containsKey(subtask.getId()) && subtask != null) {
            subtasks.put(subtask.getId(), subtask);
            for(Epic epic : epics.values()) {
                for(int idOfSubtask : epic.getSubtasksIds()) {
                    if (idOfSubtask == subtask.getId()) {
                        updateStatus(epic);
                    }
                }
            }
        } else {
            System.out.println("Некорректный ввод");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if(epics.containsKey(epic.getId()) && epic != null) {
            epics.put(epic.getId(), epic);
            updateStatus(epic);
        } else {
            System.out.println("Некорректный ввод");
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)){
            tasks.remove(id);
        } else {
            System.out.println("Нет такого идентификатора");
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)){
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtasksIds().remove((Integer) subtask.getId());
            updateStatus(epic);
            subtasks.remove(id);
        } else {
            System.out.println("Нет такого идентификатора");
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)){

            System.out.println(epics);
            Epic epic = epics.get(id);
            for (int idOfSubtask : epic.getSubtasksIds()) {
                subtasks.remove(idOfSubtask);
            }
            epics.remove(id);
        } else {
            System.out.println("Нет такого идентификатора");
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Integer> listOfSubtasks = epic.getSubtasksIds();
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        for(int i : listOfSubtasks) {
            subtasksOfEpic.add(subtasks.get(i));
        }
        return subtasksOfEpic;
    }

    @Override
    public void updateStatus(Epic epic) {
        List<Subtask> subtaskOfEpic = getSubtasksByEpic(epic);
        List<Status> listOfStatuses = new ArrayList<>();
        for(Subtask subtask : subtaskOfEpic) {
            listOfStatuses.add(subtask.getStatus());
        }
        int newCounter = 0;
        int doneCounter = 0;
        for(Status status : listOfStatuses) {
            if (status.equals(Status.NEW)) {
                newCounter++;
            } else if (status.equals(Status.DONE)) {
                doneCounter++;
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
        if (newCounter == listOfStatuses.size()) {
            epic.setStatus(Status.NEW);
        } else if (doneCounter == listOfStatuses.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void remove(int id) {
        historyManager.remove(id);
        if(epics.containsKey(id)) {
            for (int idOfSubtask : epics.get(id).getSubtasksIds()) {
                historyManager.remove(idOfSubtask);
            }
        }
    }

    public void addToHistory(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        } else if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        } else {
            historyManager.add(epics.get(id));
        }
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}