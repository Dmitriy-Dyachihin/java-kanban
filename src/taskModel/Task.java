package taskModel;

public class Task {
    private String title;
    private String description;
    private String status;
    private int taskId;

    public Task(String title, String description, String status) {
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

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(int taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public int getId() {
        return taskId;
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
