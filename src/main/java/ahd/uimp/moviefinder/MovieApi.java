package ahd.uimp.moviefinder;

import com.google.gson.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class MovieApi {
    public record Movie(long id, String url, String name, String type, int year, String description, String coverUrl, List<Link> links) {}
    public record Link(String url, long size, int quality, Duration duration) {}

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final HttpClient client = HttpClient.newHttpClient();

    @Contract("_ -> new")
    public static @NotNull List<Movie> searchInMovieBox(String searchQuery) {
        return new ArrayList<>() {{
            try {
                JsonParser.parseString(client.send(HttpRequest.newBuilder().
                                uri(URI.create(String.format("https://movieboxapp.ir/api/search/%s/a/", URLEncoder.
                                        encode(searchQuery, StandardCharsets.UTF_8)))).build(), HttpResponse.BodyHandlers.ofString()).body()).
                        getAsJsonObject().getAsJsonArray("posters").forEach(movie -> {
                            var m = movie.getAsJsonObject();
                            add(new Movie(m.get("id").getAsLong(), null, m.get("title").getAsString(), switch (m.get("type").getAsString()) {
                                case "serie" -> "series";
                                case "movie" -> "movie";
                                default -> throw new RuntimeException("AHD:: Unknown movie type");
                            }, m.get("year").getAsInt(), m.get("description").getAsString(), m.get("image").getAsString(),
                                    new ArrayList<>() {{
                                        var duration = m.get("duration");
                                        var min = duration.isJsonNull() ? -1 : duration.getAsString().contains(" ") ?
                                                Integer.parseInt(duration.getAsString().substring(0, duration.getAsString().indexOf(' '))) : duration.getAsInt();
                                        m.get("sources").
                                                getAsJsonArray().forEach(e -> {
                                                    var mm = e.getAsJsonObject();
                                                    var url = mm.get("url").getAsString();
                                                    var quality = mm.get("quality").getAsString();
                                                    add(
                                                            new Link(
                                                                    url,
                                                                    -1,
                                                                    quality.contains("720") ? 720 : quality.contains("1080") ? 1080 : 480,
                                                                    Duration.ofMinutes(min)
                                                            )
                                                    );
                                                });
                                    }}));
                        });
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }};
    }

    private String getBodyAsString(String url) {
        try {
            return client.send(HttpRequest.newBuilder().uri(URI.create(url)).build(), HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("AHD:: Cannot get response from: " + url);
        }
    }

    private JsonObject getAsJsonObject(String url) {
        return JsonParser.parseString(getBodyAsString(url)).getAsJsonObject();
    }

    private String beautifyJsonString(String json) {
        return gson.toJson(json);
    }
}
