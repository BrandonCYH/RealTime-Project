package com.example.realtimeproject.telegrambot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class YouTubeAPIClient {
    private static final OkHttpClient client = new OkHttpClient();

    public JsonObject getJsonFromUrl(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch data from " + url + ". HTTP Code: " + response.code());
            }
            String jsonString = response.body().string();
            return JsonParser.parseString(jsonString).getAsJsonObject();
        }
    }
}
