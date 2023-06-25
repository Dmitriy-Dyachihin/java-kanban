import taskModel.Epic;
import taskModel.Subtask;
import taskModel.Task;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        Task task1 = new Task("Задача 1", "Описание 1", "NEW");
        Task task2 = new Task("Задача 2", "Описание 2", "IN_PROGRESS");

        Epic epic1 = new Epic("Эпик 1", "Описание эпик 1", "DONE");
        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", "DONE");
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", "IN_PROGRESS");

        Epic epic2 = new Epic("Эпик 2", "Описание эпик 2", "DONE");
        Subtask subtask3 = new Subtask("Подзадача 2.1", "Описание подзадачи 2.1", "DONE");

        manager.createTask(task1);
        manager.createTask(task2);
        System.out.println(manager.getListsOfTasks());
        System.out.println(manager.getTaskById(1));
        System.out.println(manager.getTaskById(2));
        manager.createEpic(epic1);
        manager.createSubtask(subtask1, epic1);
        manager.createSubtask(subtask2, epic1);
        System.out.println(manager.getListsOfEpics());
        System.out.println(manager.getListsOfSubTasks());
        manager.createEpic(epic2);
        manager.createSubtask(subtask3, epic2);
        System.out.println(manager.getListsOfEpics());
        System.out.println(manager.getListsOfSubTasks());
        System.out.println(manager.getListOfTasksByEpic(epic2));
        System.out.println(manager.getSubtaskById(4));
        System.out.println(manager.getEpicById(3));
        manager.removeTaskById(1);
        System.out.println(manager.getListsOfTasks());
        manager.removeSubtaskById(5);
        System.out.println(manager.getListsOfEpics());
        System.out.println(manager.getListsOfSubTasks());
        manager.removeEpicById(6);
        System.out.println(manager.getListsOfEpics());
        System.out.println(manager.getListsOfSubTasks());
        manager.removeAllSubtasks();
        System.out.println(manager.getListsOfSubTasks());
        manager.removeAllTasks();
        System.out.println(manager.getListsOfTasks());
        manager.removeAllEpics();
        System.out.println(manager.getListsOfEpics());
    }
}
