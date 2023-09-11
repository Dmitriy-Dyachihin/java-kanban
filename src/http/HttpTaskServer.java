package http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpServer;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;
import tasks.Task;
import tasks.Subtask;
import tasks.Epic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class HttpTaskServer  {
    private final HttpServer httpServer;
    private static final int PORT = 8080;

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static HistoryManager historyManager = Managers.getDefaultHistory();
    private static TaskManager taskManager;

    static {
        try {
            taskManager = Managers.getDefault(historyManager);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpTaskServer() throws IOException, InterruptedException {
        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task/", new TaskHandler());
        httpServer.createContext("/tasks/epic/", new EpicHandler());
        httpServer.createContext("/tasks/subtask/", new SubtaskHandler());
        httpServer.createContext("/tasks/subtask/epic/", new SubtaskByEpicHandler());
        httpServer.createContext("/tasks/history/", new HistoryHandler());
        httpServer.createContext("/tasks/", new TasksHandler());
    }

    static class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            int statusCode;
            String response;
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            System.out.println(method + path);

            switch (method) {
                case "GET":
                    String query = httpExchange.getRequestURI().getQuery();
                    if (query == null) {
                        statusCode = 200;
                        String jsonString = gson.toJson(taskManager.getTasks());
                        System.out.println("Запрошенная задача: " + jsonString);
                        response = gson.toJson(jsonString);
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            Task task = taskManager.getTaskById(id);
                            if (task != null) {
                                response = gson.toJson(task);
                            } else {
                                response = "Нет задачи с таким id";
                            }
                            statusCode = 200;
                        } catch (StringIndexOutOfBoundsException e) {
                            statusCode = 400;
                            response = "Не указан id";
                        } catch (NumberFormatException e) {
                            statusCode = 400;
                            response = "Некорректный id";
                        }
                    }
                    break;
                case "POST":
                    String bodyRequest = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        Task task = gson.fromJson(bodyRequest, Task.class);
                        int id = task.getId();
                        if (taskManager.getTaskById(id) != null) {
                            taskManager.updateTask(task);
                            statusCode = 201;
                            response = "Оновлена задача с id=" + id;
                        } else {
                            Task taskCreated = taskManager.createTask(task);
                            System.out.println("Созданная задача: " + taskCreated);
                            int idNew = taskCreated.getId();
                            statusCode = 201;
                            response = "Создана задача с id=" + idNew;
                        }
                    } catch (JsonSyntaxException e) {
                        statusCode = 400;
                        response = "Некорректный запрос";
                    }
                    break;
                case "DELETE":
                    response = "";
                    query = httpExchange.getRequestURI().getQuery();
                    if (query == null) {
                        taskManager.removeAllTasks();
                        statusCode = 200;
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            taskManager.removeTaskById(id);
                            statusCode = 200;
                        } catch (StringIndexOutOfBoundsException e) {
                            statusCode = 400;
                            response = "Не указан id";
                        } catch (NumberFormatException e) {
                            statusCode = 400;
                            response = "Некорректный id";
                        }
                    }
                    break;
                default:
                    statusCode = 400;
                    response = "Некорректный запрос";
            }

            httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
            httpExchange.sendResponseHeaders(statusCode, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class EpicHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            int statusCode;
            String response;

            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        statusCode = 200;
                        String jsonString = gson.toJson(taskManager.getEpics());
                        System.out.println("Запрошенный эпик: " + jsonString);
                        response = gson.toJson(jsonString);
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            Epic epic = taskManager.getEpicById(id);
                            if (epic != null) {
                                response = gson.toJson(epic);
                            } else {
                                response = "Нет эпика с указанным id";
                            }
                            statusCode = 200;
                        } catch (StringIndexOutOfBoundsException e) {
                            statusCode = 400;
                            response = "Не указан id";
                        } catch (NumberFormatException e) {
                            statusCode = 400;
                            response = "Некорректный id";
                        }
                    }
                    break;
                case "POST":
                    String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        Epic epic = gson.fromJson(bodyRequest, Epic.class);
                        int id = epic.getId();
                        if (taskManager.getEpicById(id) != null) {
                            taskManager.updateTask(epic);
                            statusCode = 200;
                            response = "Обновлен эпик с id=" + id;
                        } else {
                            Epic epicCreated = taskManager.createEpic(epic);
                            System.out.println("Созданный эпик: " + epicCreated);
                            int idNew = epicCreated.getId();
                            statusCode = 201;
                            response = "Создан эпик с id=" + idNew;
                        }
                    } catch (JsonSyntaxException e) {
                        statusCode = 400;
                        response = "Некорректный запрос";
                    }
                    break;
                case "DELETE":
                    response = "";
                    query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        taskManager.removeAllEpics();
                        statusCode = 200;
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            taskManager.removeEpicById(id);
                            statusCode = 200;
                        } catch (StringIndexOutOfBoundsException e) {
                            statusCode = 400;
                            response = "Не указан id";
                        } catch (NumberFormatException e) {
                            statusCode = 400;
                            response = "Некорректный id";
                        }
                    }
                    break;
                default:
                    statusCode = 400;
                    response = "Некорректный запрос";
            }

            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
            exchange.sendResponseHeaders(statusCode, 0);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class SubtaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            int statusCode;
            String response;

            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        statusCode = 200;
                        response = gson.toJson(taskManager.getSubtasks());
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            Subtask subtask = taskManager.getSubtaskById(id);
                            if (subtask != null) {
                                response = gson.toJson(subtask);
                            } else {
                                response = "Нет подзадачи с указанным id";
                            }
                            statusCode = 200;
                        } catch (StringIndexOutOfBoundsException e) {
                            statusCode = 400;
                            response = "Не указан id";
                        } catch (NumberFormatException e) {
                            statusCode = 400;
                            response = "Некорректный id";
                        }
                    }
                    break;
                case "POST":
                    String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        Subtask subtask = gson.fromJson(bodyRequest, Subtask.class);
                        int id = subtask.getId();
                        if (taskManager.getSubtaskById(id) != null) {
                            taskManager.updateTask(subtask);
                            statusCode = 200;
                            response = "Обновлена подзадача с id=" + id;
                        }
                        else {
                            Subtask subtaskCreated = taskManager.createSubtask(subtask);
                            System.out.println("Созданная подзадача: " + subtaskCreated);
                            int idNew = subtaskCreated.getId();
                            statusCode = 201;
                            response = "Создана подзадача с id=" + idNew;
                        }
                    } catch (JsonSyntaxException e) {
                        response = "Некорректный запрос";
                        statusCode = 400;
                    }
                    break;
                case "DELETE":
                    response = "";
                    query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        taskManager.removeAllSubtasks();
                        statusCode = 200;
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            taskManager.removeSubtaskById(id);
                            statusCode = 200;
                        } catch (StringIndexOutOfBoundsException e) {
                            statusCode = 400;
                            response = "Не указан id";
                        } catch (NumberFormatException e) {
                            statusCode = 400;
                            response = "Некорректный id";
                        }
                    }
                    break;
                default:
                    statusCode = 400;
                    response = "Некорректный запрос";
            }

            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
            exchange.sendResponseHeaders(statusCode, 0);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class SubtaskByEpicHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            int statusCode = 400;
            String response;
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            System.out.println(method + path);

            if (method.equals("GET")) {
                String query = httpExchange.getRequestURI().getQuery();
                try {
                    int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                    statusCode = 200;
                    response = gson.toJson(taskManager.getSubtaskById(id));
                } catch (StringIndexOutOfBoundsException | NullPointerException e) {
                    response = "Не указан id";
                } catch (NumberFormatException e) {
                    response = "Некорректный id";
                }
            } else {
                response = "Некорректный запрос";
            }

            httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
            httpExchange.sendResponseHeaders(statusCode, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            int statusCode = 400;
            String response;
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            System.out.println(method + path);

            if (method.equals("GET")) {
                statusCode = 200;
                response = gson.toJson(taskManager.getHistory());
            } else {
                response = "Некорректный запрос";
            }

            httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
            httpExchange.sendResponseHeaders(statusCode, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            int statusCode = 400;
            String response;
            String method = httpExchange.getRequestMethod();
            String path = String.valueOf(httpExchange.getRequestURI());

            System.out.println(method + path);

            if (method.equals("GET")) {
                statusCode = 200;
                response = gson.toJson(taskManager.getPrioritizedTasks());
            } else {
                response = "Некорректный запрос";
            }

            httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
            httpExchange.sendResponseHeaders(statusCode, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }


    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }
}