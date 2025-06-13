package com.example.realtimeproject.Service;

import com.example.realtimeproject.Controller.YouTubeAPIClient;
import com.example.realtimeproject.Controller.YouTubeApiConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import org.jvnet.hk2.annotations.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class YouTubeService {
    private final YouTubeAPIClient apiClient = new YouTubeAPIClient();

    public String getInfoFromYouTubeLink(String url) throws IOException {
        String videoId = extractVideoId(url);
        if (videoId != null) {
            return getVideoInfo(videoId); // You can implement this separately
        }

        String channelId = extractChannelId(url);
        if (channelId != null) {
            return fetchChannelInfo(channelId);
        }

        return new JSONObject().put("error", "❌ Unrecognized YouTube link format.").toString();
    }

    public String getVideoInfo(String videoId) throws IOException {
        // 🛠️ This must point to the video info API URL
        String url = String.format(YouTubeApiConstants.VIDEOS_URL, videoId);
        JsonObject json = apiClient.getJsonFromUrl(url);
        JsonArray items = json.getAsJsonArray("items");

        if (items == null || items.size() == 0) {
            return "<div class='alert alert-danger'>❌ Video not found.</div>";
        }

        JsonObject video = items.get(0).getAsJsonObject();
        JsonObject snippet = video.getAsJsonObject("snippet");
        JsonObject statistics = video.getAsJsonObject("statistics");

        String title = snippet.get("title").getAsString();
        String channelTitle = snippet.get("channelTitle").getAsString();
        String views = statistics.has("viewCount") ? statistics.get("viewCount").getAsString() : "N/A";
        String likes = statistics.has("likeCount") ? statistics.get("likeCount").getAsString() : "N/A";

        return String.format(
                """
                <div class="card mb-3 shadow-sm">
                    <div class="card-body">
                        <h5 class="card-title">🎬 %s</h5>
                        <p class="card-text">
                            <strong>📺 Channel:</strong> %s<br>
                            <strong>👁️ Views:</strong> %s<br>
                            <strong>👍 Likes:</strong> %s
                        </p>
                    </div>
                </div>
                """,
                title, channelTitle, views, likes
        );
    }

    public String extractVideoId(String url) {
        try {
            if (url.contains("youtu.be/")) {
                return url.substring(url.lastIndexOf("/") + 1).split("[?&]")[0];
            }

            if (url.contains("youtube.com/watch")) {
                URI uri = new URI(url);
                String query = uri.getQuery();
                if (query != null) {
                    for (String param : query.split("&")) {
                        if (param.startsWith("v=")) {
                            return param.substring(2);
                        }
                    }
                }
            }

            if (url.contains("youtube.com/shorts/")) {
                return url.substring(url.lastIndexOf("/") + 1).split("[?&]")[0];
            }

            if (url.contains("youtube.com/embed/")) {
                return url.substring(url.lastIndexOf("/") + 1).split("[?&]")[0];
            }
        } catch (Exception e) {
            System.err.println("Error extracting video ID: " + e.getMessage());
        }
        return null;
    }

    private String extractChannelId(String url) {
        Matcher matcher = Pattern.compile("youtube\\.com/channel/([^/?&]+)").matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String fetchChannelInfo(String channelId) {
        String apiKey = YouTubeApiConstants.API_KEY;
        String apiUrl = String.format(YouTubeApiConstants.CHANNEL_URL, channelId);

        RestTemplate restTemplate = new RestTemplate();
        JSONObject response = new JSONObject(restTemplate.getForObject(apiUrl, String.class));

        JSONObject item = response.getJSONArray("items").getJSONObject(0);
        JSONObject snippet = item.getJSONObject("snippet");
        JSONObject statistics = item.getJSONObject("statistics");

        JSONObject result = new JSONObject();
        result.put("channelId", channelId);
        result.put("channelName", snippet.getString("title"));
        result.put("description", snippet.optString("description"));
        result.put("subscribers", statistics.optInt("subscriberCount", 0));
        result.put("videos", statistics.optInt("videoCount", 0));
        result.put("views", statistics.optInt("viewCount", 0));
        result.put("thumbnail", snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url"));
        result.put("publishedAt", snippet.optString("publishedAt"));

        return result.toString();
    }

}
