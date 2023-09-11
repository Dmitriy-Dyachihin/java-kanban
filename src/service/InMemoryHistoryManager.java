package service;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager{

    private final CustomLinkedList history = new CustomLinkedList();

    private class CustomLinkedList {
        private final Map<Integer, Node> elements = new HashMap<>();
        private Node head;
        private Node tail;

        private void linkLast(Task task) {
            Node element = new Node();
            element.setData(task);


            removeNode(elements.get(task.getId()));

            if (head != null) {
                element.setPrev(tail);
                element.setNext(null);
                tail.setNext(element);
                tail = element;
            } else {
                tail = element;
                head = element;
                element.setNext(null);
                element.setPrev(null);
            }
            elements.put(task.getId(), element);
        }

        private List<Task> getTasks () {
            List<Task> tasks = new ArrayList<>();
            Node element = head;
            while (element != null) {
                tasks.add(element.getData());
                element = element.getNext();
            }
            return tasks;
        }

        private void removeNode(Node node) {
            if (node != null) {
                elements.remove(node.getData().getId());
                Node prev = node.getPrev();
                Node next = node.getNext();

                if (head == node) {
                    head = node.getNext();
                }
                if (tail == node) {
                    tail = node.getPrev();
                }

                if (prev != null) {
                    prev.setNext(next);
                }

                if (next != null) {
                    next.setPrev(prev);
                }
            }
        }

        private Node getNode(int id) {
            return elements.get(id);
        }
    }

    class Node  {

        public Task data;
        public Node next;
        public Node prev;

        public Task getData() {
            return data;
        }

        public Node getNext() {
            return next;
        }

        public Node getPrev() {
            return prev;
        }

        public void setData(Task data) {
            this.data = data;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }
    }

    @Override
    public void add(Task task) {
        history.linkLast(task);
    }

    @Override
    public void remove(int id) {
        history.removeNode(history.getNode(id));
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }
}