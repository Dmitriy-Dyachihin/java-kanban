package service;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import taskStatus.Status;
import taskStatus.Type;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager{

    private File file;
    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
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
            writer.write("id,type,name,status,description,startTime,duration,epic\n");

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

                if(!line.equals("id,type,name,status,description,startTime,duration,epic")) {
                    Task task = fromString(line);

                    if (task instanceof Epic epic) {
                        addEpic(epic);
                    } else if (task instanceof Subtask subtask) {
                        addSubtask(subtask);
                    } else {
                        addTask(task);
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
                task.getStatus().toString(), task.getDescription(), String.valueOf(task.getStartTime()),
                String.valueOf(task.getDuration()), getEpicIdOfSubtask(task));
    }

    private Task fromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        String type = split[1];
        String title = split[2];
        Status status = Status.valueOf(split[3].toUpperCase());
        String description = split[4];
        Instant startTime = Instant.parse(split[5]);
        long duration = Long.parseLong(split[6]);
        Integer epicId = type.equals("SUBTASK") ? Integer.parseInt(split[7]) : null;

        if (type.equals("TASK")) {
            Task task = new Task(title, description, status, startTime, duration);
            task.setId(id);
            return task;
        } else if (type.equals("SUBTASK")) {
            Subtask subtask = new Subtask(title, description, status, epicId, startTime, duration);
            subtask.setId(id);
            return subtask;
        } else {
            Epic epic = new Epic(title, description, status, startTime, duration);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        }
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
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
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
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

    public Task addTask(Task task) {
        return super.createTask(task);
    }

    public Epic addEpic(Epic epic) {
        return super.createEpic(epic);
    }

    public Subtask addSubtask(Subtask subtask) {
        return super.createSubtask(subtask);
    }
}