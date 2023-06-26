package service;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class TaskManager {

    int id = 0;

    Map<Integer, Task> tasks = new HashMap<>(); // Список для хранения задач
    Map<Integer, Epic> epics = new HashMap<>(); // Список для хранения эпиков
    Map<Integer, Subtask> subtasks = new HashMap<>(); // Список для хранения подзадач

    int assignId() {
        return ++id;
    }
    public void createTask(Task task) {
        if(task == null) {
            System.out.println("Некорректный ввод");
        } else {
            int idOfNewTask = assignId();
            task.setId(idOfNewTask);
            tasks.put(idOfNewTask, task);
        }
    }

    public void createSubtask(Subtask subtask) {
        if(subtask == null) {
            System.out.println("Некорректный ввод");
        } else {
            int idOfNewSubtask = assignId();
            subtask.setId(idOfNewSubtask);
            Epic epic = epics.get(subtask.getEpicId());
            if(epic != null) {
                subtasks.put(idOfNewSubtask, subtask);
                epic.setSubtasksIds(idOfNewSubtask);
                updateStatus(epic);
            } else {
                System.out.println("Не существует заданного эпика");
            }
        }
    }

    public void createEpic(Epic epic) {
        if(epic == null) {
            System.out.println("Некорректный ввод");
        } else {
            int idOfNewEpic = assignId();
            epic.setId(idOfNewEpic);
            epics.put(idOfNewEpic, epic);
            updateStatus(epic);
        }
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        for(Epic epic : epics.values()) {
            epic.getSubtasksIds().clear();
            updateStatus(epic);
        }
        subtasks.clear();
    }

    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void updateTask(Task task) {
        if(tasks.containsKey(task.getId()) && task != null) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Некорректный ввод");
        }
    }

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

    public void updateEpic(Epic epic) {
        if(epics.containsKey(epic.getId()) && epic != null) {
            epics.put(epic.getId(), epic);
            updateStatus(epic);
        } else {
            System.out.println("Некорректный ввод");
        }
    }

    public void removeTaskById(int id) {
        if (tasks.containsKey(id)){
            tasks.remove(id);
        } else {
            System.out.println("Нет такого идентификатора");
        }
    }

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

    public ArrayList<Subtask> getTasksByEpic(Epic epic) {
        List<Integer> listOfSubtasks = epic.getSubtasksIds();
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        for(int i : listOfSubtasks) {
            subtasksOfEpic.add(subtasks.get(i));
        }
        return subtasksOfEpic;
    }

    public void updateStatus(Epic epic) {
        ArrayList<Subtask> subtaskOfEpic = getTasksByEpic(epic);
        ArrayList<String> listOfStatuses = new ArrayList<>();
        for(Subtask subtask : subtaskOfEpic) {
            listOfStatuses.add(subtask.getStatus());
        }
        int newCounter = 0;
        int doneCounter = 0;
        for(String status : listOfStatuses) {
            if (status.equals("NEW")) {
                newCounter++;
            } else if (status.equals("DONE")) {
                doneCounter++;
            } else {
                epic.setStatus("IN_PROGRESS");
            }
        }
        if (newCounter == listOfStatuses.size()) {
            epic.setStatus("NEW");
        } else if (doneCounter == listOfStatuses.size()) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
    }
}
