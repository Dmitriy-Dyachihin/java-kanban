package tasks;

import taskStatus.Status;

import java.util.ArrayList;
import java.util.List;
public class Epic extends Task {

    private final List<Integer> subtasksIds = new ArrayList<>();
    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public void setSubtasksIds(int id) {
        subtasksIds.add(id);
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    @Override
    public String toString() {
        return "Эпик{" +
                "Цель '" + getTitle() + '\'' +
                ", Описание '" + getDescription() + '\'' +
                ", id эпика '" + getId() + '\'' +
                ", Статус '" + getStatus() + '\'' +
                ", Список id подзадач '" + getSubtasksIds() + '\'' +
                '}';
    }
}