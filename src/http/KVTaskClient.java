package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {

    private final String url;
    private final String token;

    public KVTaskClient(String serverURL) throws IOException, InterruptedException {
        this.url = serverURL;

        URI uri = URI.create(this.url + "/register");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        token = response.body();
    }

    public void put(String key, String json) {
        URI uri = URI.create(this.url + "/save/" + key + "?API_TOKEN=" + token);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            if (response.statusCode() != 200) {
                System.out.println("Не удалось сохранить данные");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String load(String key) {
        URI uri = URI.create(this.url + "/load/" + key + "?API_TOKEN=" + token);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Ошибка во время запроса!";
        }
    }
}