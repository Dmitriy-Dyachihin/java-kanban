package taskModel;

public class Subtask extends Task {

    public Subtask(String title, String description, String status) {
        super(title, description, status);
    }

    @Override
    public String toString() {
        return "Подзадача{" +
                "Цель '" + getTitle() + '\'' +
                ", Описание '" + getDescription() + '\'' +
                ", id подзадачи '" + getId() + '\'' +
                ", Статус '" + getStatus() + '\'' +
                '}';
    }
}
