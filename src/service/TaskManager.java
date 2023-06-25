package service;

import taskModel.Epic;
import taskModel.Subtask;
import taskModel.Task;

import java.util.HashMap;
import java.util.ArrayList;
public class TaskManager {

    int id = 0;

    HashMap<Integer, Task> tasks = new HashMap<>(); // Список для хранения задач
    HashMap<Integer, Epic> epics = new HashMap<>(); // Список для хранения эпиков
    HashMap<Integer, Subtask> subtasks = new HashMap<>(); // Список для хранения подзадач

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

    public void createSubtask(Subtask subtask, Epic epic) {
        if(subtask == null) {
            System.out.println("Некорректный ввод");
        } else {
            int idOfNewSubtask = assignId();
            subtask.setId(idOfNewSubtask);
            subtasks.put(idOfNewSubtask, subtask);
            epic.setListOfSubtasks(idOfNewSubtask);
            updateStatus(epic);
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

    public ArrayList<Task> getListsOfTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getListsOfSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getListsOfEpics() {
        return new ArrayList<>(epics.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for(Epic epic : epics.values()) {
            epic.getListOfSubtasks().clear();
            updateStatus(epic);
        }
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
        if(tasks.containsKey(task.getId()) || task != null) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Некорректный ввод");
        }
    }

    public void updateSubtask(Subtask subtask) {
        if(subtasks.containsKey(subtask.getId()) || subtask != null) {
            subtasks.put(subtask.getId(), subtask);
            for(Epic epic : epics.values()) {
                for(int idOfSubtask : epic.getListOfSubtasks()) {
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
        if(epics.containsKey(epic.getId()) || epic != null) {
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
            for(Epic epic : epics.values()) {
                for(int idOfSubtask : epic.getListOfSubtasks()) {
                    if (idOfSubtask == id) {
                        updateStatus(epic);
                    }
                }
            }
            subtasks.remove(id);
        } else {
            System.out.println("Нет такого идентификатора");
        }
    }

    public void removeEpicById(int id) {
        if (epics.containsKey(id)){

            System.out.println(epics);
            Epic epic = epics.get(id);
            for (int idOfSubtask : epic.getListOfSubtasks()) {
                subtasks.remove(idOfSubtask);
            }
            epics.remove(id);
            System.out.println(subtasks);
        } else {
            System.out.println("Нет такого идентификатора");
        }
    }

    public ArrayList<Subtask> getListOfTasksByEpic(Epic epic) {
        ArrayList<Integer> listOfSubtasks = epic.getListOfSubtasks();
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        for(int i : listOfSubtasks) {
            subtasksOfEpic.add(subtasks.get(i));
        }
        return subtasksOfEpic;
    }

    public void updateStatus(Epic epic) {
        ArrayList<Subtask> subtaskOfEpic = getListOfTasksByEpic(epic);
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
