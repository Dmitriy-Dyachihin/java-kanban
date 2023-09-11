package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;
import taskStatus.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static KVServer kvServer;
    private static HttpTaskServer taskServer;
    private static Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();
    private static final String TASK_URL = "http://localhost:8080/tasks/task/";
    private static final String EPIC_URL = "http://localhost:8080/tasks/epic/";
    private static final String SUBTASK_URL = "http://localhost:8080/tasks/subtask/";

    @BeforeAll
    static void startServer() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskServer = new HttpTaskServer();
            taskServer.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @AfterAll
    static void stopServer() {
        kvServer.stop();
        taskServer.stop();
    }

    @BeforeEach
    void rebootServer() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_URL);
        try {
            HttpRequest request = HttpRequest.newBuilder().DELETE().uri(url).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            url = URI.create(EPIC_URL);
            request = HttpRequest.newBuilder().DELETE().uri(url).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            url = URI.create(SUBTASK_URL);
            request = HttpRequest.newBuilder().DELETE().uri(url).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetTasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_URL);
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW, Instant.now(), 0);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(url)
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().GET().uri(url).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetEpics() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_URL);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, Instant.now(),0);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(url)
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().GET().uri(url).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetSubtasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_URL);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, Instant.now(),0);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(url)
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode());
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = new Subtask("Подзадача 1 ", "Описание подзадачи 1", Status.NEW,
                        epic.getId(), Instant.now(),0);
                url = URI.create(SUBTASK_URL);

                request = HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .uri(url)
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                request = HttpRequest.newBuilder().GET().uri(url).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
                assertEquals(1, arrayTasks.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldRemoveTasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_URL);
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW, Instant.now(), 0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().DELETE().uri(url).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
            request = HttpRequest.newBuilder().GET().uri(url).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(0, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldRemoveEpics() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_URL);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, Instant.now(),0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().DELETE().uri(url).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
            request = HttpRequest.newBuilder().GET().uri(url).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(0, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldRemoveSubtasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_URL);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, Instant.now(),0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode());
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = new Subtask("Подзадача 1 ", "Описание подзадачи 1", Status.NEW,
                        epic.getId(), Instant.now(),0);
                url = URI.create(SUBTASK_URL);

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                request = HttpRequest.newBuilder().DELETE().uri(url).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(204, response.statusCode());
                request = HttpRequest.newBuilder().GET().uri(url).build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
                assertEquals(0, arrayTasks.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetTaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_URL);
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW, Instant.now(), 0);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(url)
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode());
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                task.setId(id);
                url = URI.create(TASK_URL + "?id=" + id);
                request = HttpRequest.newBuilder().GET().uri(url).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Task responseTask = gson.fromJson(response.body(), Task.class);
                assertEquals(task, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetEpicById() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_URL);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, Instant.now(),0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode());
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                epic.setId(id);
                url = URI.create(EPIC_URL + "?id=" + id);
                request = HttpRequest.newBuilder().GET().uri(url).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Epic responseTask = gson.fromJson(response.body(), Epic.class);
                assertEquals(epic, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetSubtaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_URL);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, Instant.now(),0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode());
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = new Subtask("Подзадача 1 ", "Описание подзадачи 1", Status.NEW,
                        epic.getId(), Instant.now(),0);
                url = URI.create(SUBTASK_URL);

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(201, postResponse.statusCode());
                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask.setId(id);
                    url = URI.create(SUBTASK_URL + "?id=" + id);
                    request = HttpRequest.newBuilder().GET().uri(url).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(200, response.statusCode());
                    Subtask responseTask = gson.fromJson(response.body(), Subtask.class);
                    assertEquals(subtask, responseTask);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdateTask() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_URL);
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW, Instant.now(), 0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                task.setStatus(Status.IN_PROGRESS);
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());

                url = URI.create(TASK_URL + "?id=" + id);
                request = HttpRequest.newBuilder().GET().uri(url).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Task responseTask = gson.fromJson(response.body(), Task.class);
                assertEquals(task, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdateEpic() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_URL);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, Instant.now(),0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                epic.setStatus(Status.IN_PROGRESS);
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());

                url = URI.create(EPIC_URL + "?id=" + id);
                request = HttpRequest.newBuilder().GET().uri(url).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Epic responseTask = gson.fromJson(response.body(), Epic.class);
                assertEquals(epic, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdateSubtask() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_URL);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, Instant.now(),0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode());
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                Subtask subtask = new Subtask("Подзадача 1 ", "Описание подзадачи 1", Status.NEW,
                        epic.getId(), Instant.now(),0);
                url = URI.create(SUBTASK_URL);

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask.setStatus(Status.IN_PROGRESS);
                    request = HttpRequest.newBuilder()
                            .uri(url)
                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                            .build();
                    client.send(request, HttpResponse.BodyHandlers.ofString());

                    url = URI.create(SUBTASK_URL + "?id=" + id);
                    request = HttpRequest.newBuilder().GET().uri(url).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(200, response.statusCode());
                    Subtask responseTask = gson.fromJson(response.body(), Subtask.class);
                    assertEquals(subtask, responseTask);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldRemoveTaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_URL);
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW, Instant.now(), 0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            int id = Integer.parseInt(postResponse.body());
            url = URI.create(TASK_URL + "?id=" + id);
            request = HttpRequest.newBuilder().DELETE().uri(url).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());

            request = HttpRequest.newBuilder().GET().uri(url).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("Задача с данным id не найдена", response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    void shouldRemoveEpicById() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_URL);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, Instant.now(),0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode());
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                url = URI.create(EPIC_URL + "?id=" + id);
                request = HttpRequest.newBuilder().DELETE().uri(url).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(204, response.statusCode());

                request = HttpRequest.newBuilder().GET().uri(url).build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals("Эпик с данным id не найден", response.body());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldRemoveSubtaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_URL);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", Status.NEW, Instant.now(),0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode());
            if (postResponse.statusCode() == 201) {
                Subtask subtask = new Subtask("Подзадача 1 ", "Описание подзадачи 1", Status.NEW,
                        epic.getId(), Instant.now(),0);
                url = URI.create(SUBTASK_URL);

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(201, postResponse.statusCode());
                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask.setId(id);
                    url = URI.create(SUBTASK_URL + "?id=" + id);
                    request = HttpRequest.newBuilder().DELETE().uri(url).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(204, response.statusCode());

                    request = HttpRequest.newBuilder().GET().uri(url).build();
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals("Подзадача с данным id не найдена", response.body());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}