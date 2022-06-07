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
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        new RestAPI().starter();
    }

    private void starter() throws URISyntaxException, IOException, InterruptedException {

        String apiKey = getProperty("ASSEMBLYAI_API_KEY");
        Transcript transcript = new Transcript();
        transcript.setAudio_url("https://github.com/SergeyKovalevDev/JavaAPITutorial/blob/main/Thirsty.mp4");
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(transcript);
        System.out.println(jsonRequest);
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("authorization", apiKey)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(postResponse.body());
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
