package com.usim.ulib.encubator.net.twitch.model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LinkFinder {

    // for test
    public static void main(String[] args) throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        var res = HttpRequest.
                newBuilder().
                uri(URI.create("https://clips.twitch.tv/SteamyElatedPlumFailFish-mlllYw7VUQMO0I9L"));
        var get = res.GET().build();
        var response = client.send(get, HttpResponse.BodyHandlers.ofString());
        var here = response.body();
        System.out.println(here);
    }
}
