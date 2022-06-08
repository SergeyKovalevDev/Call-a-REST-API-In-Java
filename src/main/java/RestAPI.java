import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class RestAPI {
    private final String apiKey = getProperty("ASSEMBLYAI_API_KEY");

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        new RestAPI().starter();
    }

    private void starter() throws URISyntaxException, IOException, InterruptedException {

        Transcript transcript = new Transcript();
        transcript.setAudio_url("https://github.com/SergeyKovalevDev/JavaAPITutorial/raw/main/Thirsty.mp4");
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(transcript);
        System.out.println(jsonRequest);

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("authorization", apiKey)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        transcript = gson.fromJson(postResponse.body(), Transcript.class);

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript/" + transcript.getId()))
                .header("authorization", apiKey)
                .header("content-type", "application/json")
                .build();
        do {
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            transcript = gson.fromJson(getResponse.body(), Transcript.class);
            System.out.println(transcript.getStatus());
            Thread.sleep(1000);
        } while (!transcript.getStatus().equals("completed") && !transcript.getStatus().equals("error"));

        System.out.println("Transcription completed");
        System.out.println(transcript.getText());

    }

    private String getProperty(String propertyName) {
        String propertyValue;
        Properties property = new Properties();
        try (InputStream inputStream = this.getClass().getResourceAsStream("app.properties")) {
            property.load(inputStream);
            propertyValue = property.getProperty(propertyName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return propertyValue;
    }
}
