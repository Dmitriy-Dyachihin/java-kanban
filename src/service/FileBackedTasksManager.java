package service;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import taskStatus.Status;
import taskStatus.Type;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager{

    private File file;
    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        File file = new File("source/results.csv");
        FileBackedTasksManager taskManager = new FileBackedTasksManager(file);

        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпик 1", Status.NEW);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", Status.DONE, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", Status.IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 1.3", "Описание подзадачи 1.3", Status.IN_PROGRESS, epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic("Эпик 2", "Описание эпик 2", Status.DONE);
        taskManager.createEpic(epic2);

        taskManager.getTaskById(1);
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(2);
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(1);
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(3);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(4);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(6);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(5);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(4);
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(7);
        System.out.println(taskManager.getHistory());

    }

    public void save() {
        try {
            if (Files.exists(file.toPath())) {
                Files.delete(file.toPath());
            }
            Files.createFile(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Данный файл был не найден");
        }

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (Subtask subtask : getSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }

            writer.write("\n");
            writer.write(historyToString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл");
        }
    }

    public void loadFromFile(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {

            String line;
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.equals("")) {
                    break;
                }

                if(!line.equals("id,type,name,status,description,epic")) {
                    Task task = fromString(line);

                    if (task instanceof Epic epic) {
                        createEpic(epic);
                    } else if (task instanceof Subtask subtask) {
                        createSubtask(subtask);
                    } else {
                        createTask(task);
                    }
                }
            }

            String historyToString = bufferedReader.readLine();
            for (int id : historyFromString(historyToString)) {
                addToHistory(id);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении данных из файла");
        }
    }

    static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder stringBuilder = new StringBuilder();

        if (history.isEmpty()) {
            return "";
        }

        for (Task task : history) {
            stringBuilder.append(task.getId()).append(",");
        }

        if (stringBuilder.length() != 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (value != null) {
            String[] split = value.split(",");

            for (String element : split) {
                history.add(Integer.parseInt(element));
            }

            return history;
        }
        return history;
    }

    private String toString(Task task) {
        return String.join(",", Integer.toString(task.getId()), getType(task).toString(), task.getTitle(),
                task.getStatus().toString(), task.getDescription(), getEpicIdOfSubtask(task));
    }

    private Task fromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        String type = split[1];
        String title = split[2];
        Status status = Status.valueOf(split[3].toUpperCase());
        String description = split[4];
        Integer epicId = type.equals("SUBTASK") ? Integer.parseInt(split[5]) : null;

        if (type.equals("TASK")) {
            Task task = new Task(title, description, status);
            task.setId(id);
            return task;
        } else if (type.equals("SUBTASK")) {
            Subtask subtask = new Subtask(title, description, status, epicId);
            subtask.setId(id);
            return subtask;
        } else {
            Epic epic = new Epic(title, description, status);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        save();
        return super.getTaskById(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        save();
        return super.getSubtaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        save();
        return super.getEpicById(id);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    private String getEpicIdOfSubtask(Task task) {
        if (task instanceof Subtask) {
            return Integer.toString(((Subtask) task).getEpicId());
        }
        return "";
    }

    private Type getType(Task task) {
        if (task instanceof Epic) {
            return Type.EPIC;
        } else if (task instanceof Subtask) {
            return Type.SUBTASK;
        }
        return Type.TASK;
    }
}
