package tasks;

import taskStatus.Status;

public class Task {
    private String title;
    private String description;
    private Status status;
    private int id;

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(int taskId) {
        this.id = taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Задача{" +
                " Цель '" + title + '\'' +
                ", Описание '" + description + '\'' +
                ", id задачи '" + getId() + '\'' +
                ", Статус '" + status + '\'' +
                '}';
    }
}