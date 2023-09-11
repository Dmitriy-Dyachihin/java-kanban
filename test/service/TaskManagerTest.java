package service;

import org.junit.jupiter.api.Test;
import taskStatus.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.function.Executable;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;
    protected Task createTask() {
        return new Task("Задача 1", "Описание 1", Status.NEW, Instant.now(), 0);
    }
    protected Epic createEpic() {

        return new Epic("Эпик 1", "Описание эпик 1", Status.NEW, Instant.now(), 0);
    }
    protected Subtask createSubtask(Epic epic) {
        return new Subtask("Подзадача 1.1", "Описание подзадачи 1.1",
                Status.NEW, epic.getId(), Instant.now(), 0);
    }

    @Test
    public void shouldCreateTask() {
        Task task = createTask();
        manager.createTask(task);
        List<Task> tasks = manager.getTasks();
        assertNotNull(task.getStatus());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }

    @Test
    public void shouldCreateEpic() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        List<Epic> epics = manager.getEpics();
        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(Collections.EMPTY_LIST, epic.getSubtasksIds());
        assertEquals(List.of(epic), epics);
    }

    @Test
    public void shouldCreateSubtask() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        List<Subtask> subtasks = manager.getSubtasks();
        assertNotNull(subtask.getStatus());
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(List.of(subtask), subtasks);
        assertEquals(List.of(subtask.getId()), epic.getSubtasksIds());
    }

    @Test
    public void shouldBeNewEpic(){
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldBeDoneEpic(){
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void shouldBeInProgressEpic(){
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
        assertEquals(0, epic.getDuration());
    }

    @Test
    public void shouldBeInProgressEpicToo(){
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask1 = createSubtask(epic);
        manager.createSubtask(subtask1);
        subtask1.setStatus(Status.DONE);
        subtask1.setDuration(60);
        manager.updateSubtask(subtask1);
        Subtask subtask2 = createSubtask(epic);
        manager.createSubtask(subtask2);
        subtask2.setStatus(Status.NEW);
        manager.updateSubtask(subtask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
        assertEquals(3600000, epic.getDuration());
    }

    @Test
    void shouldPassCheckIntersection() {
        Task task1 = createTask();
        manager.createTask(task1);
        task1.setDuration(60);
        manager.updateTask(task1);
        Task task2 = createTask();
        manager.createTask(task2);
        manager.updateTask(task2);
    }

    @Test
    void shouldNotPassCheckIntersection() {
        final ManagerValidateException exception = assertThrows(
                ManagerValidateException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerValidateException {
                        Task task1 = createTask();
                        manager.createTask(task1);
                        task1.setStartTime(Instant.ofEpochSecond(100000));
                        manager.updateTask(task1);
                        Task task2 = createTask();
                        manager.createTask(task2);
                        task2.setStartTime(Instant.ofEpochSecond(100000));
                        manager.updateTask(task2);
                    }
                });
    }

    @Test
    void shouldReturnTasks() {
        Task task = createTask();
        manager.createTask(task);
        manager.getTasks();
        assertEquals(List.of(task), manager.getTasks());
    }

    @Test
    void shouldReturnSubtasks() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.getSubtasks();
        assertEquals(List.of(subtask), manager.getSubtasks());
    }

    @Test
    void shouldReturnEpics() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.getEpics();
        assertEquals(List.of(epic), manager.getEpics());
    }

    @Test
    void shouldBeEmptyIfNoTasks() {
        manager.getTasks();
        assertEquals(Collections.EMPTY_LIST, manager.getTasks());
    }

    @Test
    void shouldBeEmptyIfNoSubtasks() {
        manager.getTasks();
        assertEquals(Collections.EMPTY_LIST, manager.getSubtasks());
    }

    @Test
    void shouldBeEmptyIfNoEpics() {
        manager.getTasks();
        assertEquals(Collections.EMPTY_LIST, manager.getEpics());
    }

    @Test
    void shouldNullIfTaskNull() {
        Task task = manager.createTask(null);
        assertNull(task);
    }

    @Test
    void shouldReturnNullIfEpicNull() {
        Epic epic = manager.createEpic(null);
        assertNull(epic);
    }

    @Test
    void shouldReturnNullIfSubtaskNull() {
        Subtask subtask = manager.createSubtask(null);
        assertNull(subtask);
    }

    @Test
    public void shouldUpdateTaskStatusToInProgress() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, manager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInProgress() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToInProgress() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, manager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateTaskStatusToDone() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(Status.DONE);
        manager.updateTask(task);
        assertEquals(Status.DONE, manager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToDone() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        epic.setStatus(Status.DONE);
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToInDone() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);
        assertEquals(Status.DONE, manager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldNotUpdateTaskIfNull() {
        Task task = createTask();
        manager.createTask(task);
        manager.updateTask(null);
        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    public void shouldNotUpdateEpicIfNull() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.updateEpic(null);
        assertEquals(epic, manager.getEpicById(epic.getId()));
    }

    @Test
    public void shouldNotUpdateSubtaskIfNull() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.updateSubtask(null);
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()));
    }

    @Test
    public void shouldRemoveTasks() {
        Task task = createTask();
        manager.createTask(task);
        manager.removeAllTasks();
        assertEquals(Collections.EMPTY_LIST, manager.getTasks());
    }

    @Test
    public void shouldRemoveEpics() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.removeAllEpics();
        assertEquals(Collections.EMPTY_LIST, manager.getEpics());
    }

    @Test
    public void shouldRemoveSubtasks() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.removeAllSubtasks();
        assertTrue(epic.getSubtasksIds().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    public void shouldRemoveTaskById() {
        Task task = createTask();
        manager.createTask(task);
        manager.removeTaskById(task.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getTasks());
    }

    @Test
    public void shouldRemoveEpicById() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.removeEpicById(epic.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getEpics());
    }

    @Test
    public void shouldNotRemoveTaskWithIncorrectId() {
        Task task = createTask();
        manager.createTask(task);
        manager.removeTaskById(777);
        assertEquals(List.of(task), manager.getTasks());
    }

    @Test
    public void shouldNotDeleteEpicWithIncorrectId() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.removeEpicById(777);
        assertEquals(List.of(epic), manager.getEpics());
    }

    @Test
    public void shouldNotRemoveSubtaskWithIncorrectId() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.removeSubtaskById(777);
        assertEquals(List.of(subtask), manager.getSubtasks());
        assertEquals(List.of(subtask.getId()), manager.getEpicById(epic.getId()).getSubtasksIds());
    }

    @Test
    public void shouldNotRemoveIfTaskHashMapIsEmpty(){
        manager.removeAllTasks();
        manager.removeTaskById(777);
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void shouldNotRemoveIfEpicHashMapIsEmpty(){
        manager.removeAllEpics();
        manager.removeEpicById(777);
        assertTrue(manager.getEpics().isEmpty());
    }

    @Test
    public void shouldNotRemoveIfSubtaskHashMapIsEmpty(){
        manager.removeAllEpics();
        manager.removeSubtaskById(777);
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    void shouldReturnEmptyListIfGetSubtaskByEpicIdIsEmpty() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        List<Subtask> subtasks = manager.getSubtasksByEpic(epic);
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListTasksIfNoTasks() {
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    public void shouldReturnEmptyListSubtasksIfNoSubtasks() {
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    public void shouldReturnEmptyListEpicsIfNoEpics() {
        assertTrue(manager.getEpics().isEmpty());
    }


    @Test
    public void shouldReturnNullIfTaskDoesNotExist() {
        assertNull(manager.getTaskById(777));
    }

    @Test
    public void shouldReturnNullIfEpicDoesNotExist() {
        assertNull(manager.getEpicById(777));
    }

    @Test
    public void shouldReturnNullIfSubtaskDoesNotExist() {
        assertNull(manager.getSubtaskById(777));
    }

    @Test
    public void shouldReturnEmptyHistory() {
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void shouldReturnEmptyHistoryIfTasksNotExist() {
        manager.getTaskById(777);
        manager.getSubtaskById(777);
        manager.getEpicById(777);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldReturnHistoryWithTasks() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());
        List<Task> list = manager.getHistory();
        assertEquals(2, list.size());
        assertTrue(list.contains(subtask));
        assertTrue(list.contains(epic));
    }

}