package com.example.realtimeproject.telegrambot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jvnet.hk2.annotations.Service;

import java.io.IOException;
import java.time.Instant;

@Service
public class YouTubeService {
    private final YouTubeAPIClient apiClient = new YouTubeAPIClient();

    public String fetchChannelInfo(String channelId) {
        try {
            String url = String.format(YouTubeApiConstants.CHANNEL_URL, channelId);
            JsonObject json = apiClient.getJsonFromUrl(url);
            JsonArray items = json.getAsJsonArray("items");
            if (items == null || items.size() == 0) {
                return "❌ Channel not found. Please verify the Channel ID.";
            }
            JsonObject item = items.get(0).getAsJsonObject();
            if (!item.has("statistics") || !item.has("snippet")) {
                return "❌ Incomplete channel data.";
            }
            JsonObject statistics = item.getAsJsonObject("statistics");
            JsonObject snippet = item.getAsJsonObject("snippet");

            String title = (snippet.has("title") && !snippet.get("title").isJsonNull()) ?
                    snippet.get("title").getAsString() : "N/A";
            String subscriberCount = (statistics.has("subscriberCount") && !statistics.get("subscriberCount").isJsonNull()) ?
                    statistics.get("subscriberCount").getAsString() : "N/A";
            String videoCount = (statistics.has("videoCount") && !statistics.get("videoCount").isJsonNull()) ?
                    statistics.get("videoCount").getAsString() : "N/A";
            String viewCount = (statistics.has("viewCount") && !statistics.get("viewCount").isJsonNull()) ?
                    statistics.get("viewCount").getAsString() : "N/A";

            return String.format(
                    "📺 Channel: %s\n👥 Subscribers: %s\n📹 Videos: %s\n👁️‍ Total views: %s",
                    title, subscriberCount, videoCount, viewCount
            );
        } catch (IOException e) {
            return "⚠️ Error: " + e.getMessage();
        }
    }

    public String getChannelGrowth(String channelId) {
        try {
            String url = String.format(YouTubeApiConstants.CHANNEL_URL, channelId);
            JsonObject json = apiClient.getJsonFromUrl(url);
            JsonArray items = json.getAsJsonArray("items");
            if (items == null || items.size() == 0) {
                return "❌ Channel not found.";
            }
            JsonObject stats = items.get(0).getAsJsonObject().getAsJsonObject("statistics");
            if (!stats.has("subscriberCount") || !stats.has("viewCount")) {
                return "❌ Incomplete statistics information.";
            }
            long subs = Long.parseLong(stats.get("subscriberCount").getAsString());
            long views = Long.parseLong(stats.get("viewCount").getAsString());
            return String.format(
                    "📈 Growth Analysis:\n" +
                            "▶️ Subscriber-to-View Ratio: 1:%,d\n" +
                            "▶️ Estimated Daily Growth: ~%,d subs/day\n" +
                            "▶️ Estimated Monthly Growth: ~%,d views/month",
                    views / Math.max(1, subs),
                    subs / 1000,
                    views / 100
            );
        } catch (Exception e) {
            return "⚠️ Could not analyze growth: " + e.getMessage();
        }
    }

    public String getUploadFrequency(String channelId) {
        try {
            String url = String.format(YouTubeApiConstants.VIDEOS_URL, channelId);
            JsonObject json = apiClient.getJsonFromUrl(url);
            JsonArray items = json.getAsJsonArray("items");
            if (items == null || items.size() < 2) {
                return "Not enough videos to determine frequency.";
            }
            JsonObject snippet1 = items.get(0).getAsJsonObject().getAsJsonObject("snippet");
            JsonObject snippet2 = items.get(1).getAsJsonObject().getAsJsonObject("snippet");
            Instant date1 = Instant.parse(snippet1.get("publishedAt").getAsString());
            Instant date2 = Instant.parse(snippet2.get("publishedAt").getAsString());

            String timeBetweenStr = TimeFormatter.formatTimeDifference(date2, date1);
            String lastUploadStr = TimeFormatter.formatElapsedTime(date1);
            return String.format(
                    "⏱️ Upload Frequency:\n" +
                            "▶️ Time between last uploads: %s\n" +
                            "▶️ Last video uploaded: %s",
                    timeBetweenStr, lastUploadStr
            );
        } catch (Exception e) {
            return "⚠️ Could not analyze frequency: " + e.getMessage();
        }
    }

    public String getChannelIdFromUsername(String username) {
        try {
            String url = String.format(YouTubeApiConstants.SEARCH_URL, username);
            JsonObject json = apiClient.getJsonFromUrl(url);
            JsonArray items = json.getAsJsonArray("items");
            if (items == null || items.size() == 0) {
                return null;
            }

            for (JsonElement element : items) {
                JsonObject idObj = element.getAsJsonObject().getAsJsonObject("id");
                if (idObj != null && idObj.has("channelId") && !idObj.get("channelId").isJsonNull()) {
                    return idObj.get("channelId").getAsString();
                }
            }
            return null;
        } catch (IOException e) {
            System.err.println("Error fetching channel ID: " + e.getMessage());
            return null;
        }
    }
}

