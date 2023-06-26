import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        Task task1 = new Task("Задача 1", "Описание 1", "NEW");
        Task task2 = new Task("Задача 2", "Описание 2", "IN_PROGRESS");
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпик 1", "DONE");
        manager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", "DONE", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", "IN_PROGRESS", epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Описание эпик 2", "DONE");
        manager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Подзадача 2.1", "Описание подзадачи 2.1", "DONE", epic2.getId());
        manager.createSubtask(subtask3);

        System.out.println(manager.getTasks());
        System.out.println(manager.getTaskById(1));
        System.out.println(manager.getTaskById(2));
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());
        task1.setTitle("Новая задача 1");
        manager.updateTask(task1);
        subtask3.setTitle("Новая подзадача 2.1");
        manager.updateSubtask(subtask3);
        epic2.setTitle("Новый эпик 2");
        manager.updateEpic(epic2);
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());
        System.out.println(manager.getTasksByEpic(epic2));
        System.out.println(manager.getSubtaskById(4));
        System.out.println(manager.getEpicById(3));
        manager.removeTaskById(1);
        System.out.println(manager.getTasks());
        manager.removeSubtaskById(5);
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());
        manager.removeEpicById(6);
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());
        manager.removeAllSubtasks();
        System.out.println(manager.getSubTasks());
        manager.removeAllTasks();
        System.out.println(manager.getTasks());
        manager.removeAllEpics();
        System.out.println(manager.getEpics());
    }
}