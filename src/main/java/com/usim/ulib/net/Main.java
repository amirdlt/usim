package com.usim.ulib.net;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        var client = HttpClient.newHttpClient();

        var response = client.
                send(HttpRequest.newBuilder()
                        .POST(buildFormDataFromMap(Map.of("Amir", "hossein")))
                        .uri(URI.create("https://httpbin.org/post"))
                        .setHeader("User-Agent", "...") // add request header
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .build(), HttpResponse.BodyHandlers.ofString());

        System.out.println(response.uri());
        System.out.println(response.body());
        System.out.println(response.statusCode());
        System.out.println(response.version());
        for (var kv : response.headers().map().entrySet())
            System.out.println(kv.getKey() + ": " + kv.getValue());
    }

    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (var entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
