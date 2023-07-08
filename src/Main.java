import service.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import service.InMemoryTaskManager;
import taskStatus.Status;

public class Main {
    public static void main(String[] args) {

        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпик 1", Status.NEW);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1.1", "Описание подзадачи 1.1", Status.DONE, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 1.2", "Описание подзадачи 1.2", Status.IN_PROGRESS, epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Описание эпик 2", Status.DONE);
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Подзадача 2.1", "Описание подзадачи 2.1", Status.DONE, epic2.getId());
        taskManager.createSubtask(subtask3);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getTaskById(2));
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        task1.setTitle("Новая задача 1");
        taskManager.updateTask(task1);
        subtask3.setTitle("Новая подзадача 2.1");
        taskManager.updateSubtask(subtask3);
        epic2.setTitle("Новый эпик 2");
        taskManager.updateEpic(epic2);
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getSubtasksByEpic(epic2));
        System.out.println(taskManager.getSubtaskById(4));
        System.out.println(taskManager.getEpicById(3));
        taskManager.removeTaskById(1);
        System.out.println(taskManager.getTasks());
        taskManager.removeSubtaskById(5);
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        taskManager.removeEpicById(6);
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        taskManager.removeAllSubtasks();
        System.out.println(taskManager.getSubtasks());
        taskManager.removeAllTasks();
        System.out.println(taskManager.getTasks());
        taskManager.removeAllEpics();
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getHistory());
        System.out.println(Managers.getDefaultHistory().getHistory());
    }
}
