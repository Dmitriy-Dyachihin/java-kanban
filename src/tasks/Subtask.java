package tasks;

import taskStatus.Status;

public class Subtask extends Task {

    private int epicId;
    public Subtask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Подзадача{" +
                "Цель '" + getTitle() + '\'' +
                ", Описание '" + getDescription() + '\'' +
                ", id эпика, которому принадлежит '" + getEpicId() + '\'' +
                ", id подзадачи '" + getId() + '\'' +
                ", Статус '" + getStatus() + '\'' +
                '}';
    }
}