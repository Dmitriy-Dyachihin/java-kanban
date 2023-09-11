package tasks;

import taskStatus.Status;

import java.time.Instant;
import java.util.Objects;

public class Subtask extends Task {

    private final int epicId;
    public Subtask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, Status status, int epicId,  Instant startTime, long duration) {
        super(title, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        return "Подзадача{" +
                "Цель '" + getTitle() + '\'' +
                ", Описание '" + getDescription() + '\'' +
                ", id эпика, которому принадлежит '" + getEpicId() + '\'' +
                ", id подзадачи '" + getId() + '\'' +
                ", Статус '" + getStatus() + '\'' +
                ", startTime='" + getStartTime().toEpochMilli() + '\'' +
                ", endTime='" + getEndTime().toEpochMilli() + '\'' +
                ", duration='" + getDuration() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}