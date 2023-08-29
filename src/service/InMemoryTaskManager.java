package service;

import taskStatus.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Instant;
import java.util.*;

public class InMemoryTaskManager implements  TaskManager {

    private static int id = 0;

    private final Map<Integer, Task> tasks = new HashMap<>(); // Список для хранения задач
    private final Map<Integer, Epic> epics = new HashMap<>(); // Список для хранения эпиков
    private final Map<Integer, Subtask> subtasks = new HashMap<>(); // Список для хранения подзадач

    private final HistoryManager historyManager;
    private final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime);
    protected Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

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
        addPrioritizedTask(task);
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
                addPrioritizedTask(subtask);
                subtasks.put(idOfNewSubtask, subtask);
                epic.setSubtasksIds(idOfNewSubtask);
                updateStatus(epic);
                updateTime(epic);
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
        if (tasks.size() == 0) {
            return Collections.emptyList();
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        if (subtasks.size() == 0) {
            return Collections.emptyList();
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        if (epics.size() == 0) {
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for(Epic epic : epics.values()) {
            for(int subtaskId : epic.getSubtasksIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                prioritizedTasks.remove(subtask);
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epic.getSubtasksIds().clear();
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
            addPrioritizedTask(task);
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Некорректный ввод");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if(subtasks.containsKey(subtask.getId()) && subtask != null) {
            addPrioritizedTask(subtask);
            subtasks.put(subtask.getId(), subtask);
            for(Epic epic : epics.values()) {
                for(int idOfSubtask : epic.getSubtasksIds()) {
                    if (idOfSubtask == subtask.getId()) {
                        updateStatus(epic);
                        updateTime(epic);
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
            updateTime(epic);
        } else {
            System.out.println("Некорректный ввод");
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)){
            prioritizedTasks.removeIf(task -> task.getId() == id);
            tasks.remove(id);
            historyManager.remove(id);
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
            updateTime(epic);
            prioritizedTasks.remove(subtask);
            subtasks.remove(id);
            historyManager.remove(id);
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
                prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), idOfSubtask));
                subtasks.remove(idOfSubtask);
                historyManager.remove(idOfSubtask);
            }
            epics.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Нет такого идентификатора");
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        if(epic != null){
            List<Integer> listOfSubtasks = epic.getSubtasksIds();
            List<Subtask> subtasksOfEpic = new ArrayList<>();
            for (int i : listOfSubtasks) {
                subtasksOfEpic.add(subtasks.get(i));
            }
            return subtasksOfEpic;
        }
        return Collections.emptyList();
    }

    @Override
    public void updateStatus(Epic epic) {
        if(epics.containsKey(epic.getId())){
            if(epic.getSubtasksIds().size() == 0) {
                epic.setStatus(Status.NEW);
            } else {
                List<Subtask> subtaskOfEpic = getSubtasksByEpic(epic);
                List<Status> statuses = new ArrayList<>();
                for (Subtask subtask : subtaskOfEpic) {
                    statuses.add(subtask.getStatus());
                }
                int newCounter = 0;
                int doneCounter = 0;
                for (Status status : statuses) {
                    if (status.equals(Status.NEW)) {
                        newCounter++;
                    } else if (status.equals(Status.DONE)) {
                        doneCounter++;
                    } else {
                        epic.setStatus(Status.IN_PROGRESS);
                    }
                }
                if (newCounter == statuses.size()) {
                    epic.setStatus(Status.NEW);
                } else if (doneCounter == statuses.size()) {
                    epic.setStatus(Status.DONE);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        }
    }

    public void updateTime(Epic epic) {
        List<Subtask> subtasks = getSubtasksByEpic(epic);
        Instant start = subtasks.get(0).getStartTime();
        Instant end = subtasks.get(0).getEndTime();

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime().isBefore(start)) start = subtask.getStartTime();
            if (subtask.getEndTime().isAfter(end)) end = subtask.getEndTime();
        }

        epic.setStartTime(start);
        epic.setEndTime(end);
        long duration = (end.toEpochMilli() - start.toEpochMilli());
        epic.setDuration(duration);
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

    private void addPrioritizedTask(Task task) {
        prioritizedTasks.add(task);
        validate();
    }

    private boolean checkIntersection(Task task) {
        List<Task> tasks = List.copyOf(prioritizedTasks);
        int quantityNullTime = 0;
        if (tasks.size() > 0) {
            for (Task savedTask : tasks) {
                if (savedTask.getStartTime() != null && savedTask.getEndTime() != null) {
                    if (task.getStartTime().isBefore(savedTask.getStartTime())
                            && task.getEndTime().isBefore(savedTask.getStartTime())) {
                        return true;
                    } else if (task.getStartTime().isAfter(savedTask.getEndTime())
                            && task.getEndTime().isAfter(savedTask.getEndTime())) {
                        return true;
                    }
                } else {
                    quantityNullTime++;
                }

            }
            return quantityNullTime == tasks.size();
        } else {
            return true;
        }
    }

    private void validate() {
        List<Task> tasks = getPrioritizedTasks();

        for (int i = 1; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            boolean hasIntersections = checkIntersection(task);

            if (hasIntersections) {
                throw new ManagerValidateException(task.getId() + " и " + tasks.get(i - 1) + " задачи пересекаются");
            }
        }
    }

    private List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }
}