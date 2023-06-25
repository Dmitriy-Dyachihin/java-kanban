package taskModel;

import java.util.ArrayList;
public class Epic extends Task {

    private ArrayList<Integer> listOfSubtasks = new ArrayList<>();
    public Epic(String title, String description, String status) {
        super(title, description, status);
    }

    public void setListOfSubtasks(int id) {
        listOfSubtasks.add(id);
    }

    public ArrayList<Integer> getListOfSubtasks() {
        return listOfSubtasks;
    }

    @Override
    public String toString() {
        return "Эпик{" +
                    "Цель '" + getTitle() + '\'' +
                    ", Описание '" + getDescription() + '\'' +
                    ", id эпика '" + getId() + '\'' +
                    ", Статус '" + getStatus() + '\'' +
                    ", Список id подзадач '" + getListOfSubtasks() + '\'' +
                    '}';
    }
}
