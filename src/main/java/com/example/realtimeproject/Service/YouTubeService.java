package com.example.realtimeproject.Service;

import com.example.realtimeproject.Model.YouTubeApiConstants;
import com.example.realtimeproject.Model.YouTubeData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class YouTubeService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public YouTubeData fetchVideoData(String videoId) {
        try {
            // Step 1: Get video details
            String videoDetailsUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet,statistics&id=" + videoId + "&key=" + YouTubeApiConstants.API_KEY;
            String videoCommentUrl = "https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&videoId=" + videoId + "&key=" + YouTubeApiConstants.API_KEY;
            String videoResponse = restTemplate.getForObject(videoDetailsUrl, String.class);
            JsonNode videoJson = objectMapper.readTree(videoResponse);

            JsonNode videoItems = videoJson.get("items");
            if (videoItems == null || !videoItems.isArray() || videoItems.isEmpty()) {
                System.err.println("No video data found for video ID: " + videoId);
                return null;
            }

            JsonNode videoItem = videoItems.get(0);
            JsonNode snippet = videoItem.get("snippet");
            JsonNode statistics = videoItem.get("statistics");

            if (snippet == null || statistics == null) {
                System.err.println("Missing snippet or statistics for video ID: " + videoId);
                return null;
            }

            // Defensive access
            String videoImage = snippet.has("thumbnails") ? snippet.get("thumbnails").get("default").get("url").asText() : "No image";
            String channelId = snippet.has("channelId") ? snippet.get("channelId").asText() : null;
            String videoTitle = snippet.has("title") ? snippet.get("title").asText() : "Unknown Title";
            long likeCount = statistics.has("likeCount") ? statistics.get("likeCount").asLong() : 0;
            long commentCount = statistics.has("commentCount") ? statistics.get("commentCount").asLong() : 0;

            // Step 1.5: Get video top comment
            String commentResponse = restTemplate.getForObject(videoCommentUrl, String.class);
            JsonNode commentJson = objectMapper.readTree(commentResponse);

            String topComment = "No comments found";
            JsonNode commentItems = commentJson.get("items");
            if (commentItems != null && commentItems.isArray() && !commentItems.isEmpty()) {
                JsonNode topCommentSnippet = commentItems.get(0)
                        .get("snippet")
                        .get("topLevelComment")
                        .get("snippet")
                        .get("textDisplay");

                if (topCommentSnippet != null) {
                    topComment = topCommentSnippet.asText();
                }
            }

            if (channelId == null) {
                System.err.println("Missing channelId for video ID: " + videoId);
                return null;
            }

            // Step 2: Get channel statistics
            String channelUrl = String.format(YouTubeApiConstants.CHANNEL_URL, channelId);
            String channelResponse = restTemplate.getForObject(channelUrl, String.class);
            JsonNode channelJson = objectMapper.readTree(channelResponse);

            JsonNode channelItems = channelJson.get("items");
            if (channelItems == null || !channelItems.isArray() || channelItems.isEmpty()) {
                System.err.println("No channel data found for channel ID: " + channelId);
                return null;
            }

            JsonNode channelItem = channelItems.get(0);
            JsonNode channelSnippet = channelItem.get("snippet");
            JsonNode channelStats = channelItem.get("statistics");

            if (channelSnippet == null || channelStats == null) {
                System.err.println("Missing snippet or statistics for channel ID: " + channelId);
                return null;
            }

            String channelTitle = channelSnippet.has("title") ? channelSnippet.get("title").asText() : "Unknown Channel";
            long subscriberCount = channelStats.has("subscriberCount") ? channelStats.get("subscriberCount").asLong() : 0;
            long totalVideos = channelStats.has("videoCount") ? channelStats.get("videoCount").asLong() : 0;

            // Step 3: Combine into object
            YouTubeData data = new YouTubeData();
            data.setThumbnailUrl(videoImage);
            data.setVideoTitle(videoTitle);
            data.setVideoUrl("https://www.youtube.com/watch?v=" + videoId);
            data.setChannelName(channelTitle);
            data.setSubscriberCount(subscriberCount);
            data.setTotalVideos(totalVideos);
            data.setTotalLikes(likeCount);
            data.setComments(topComment);
            data.setTotalComments(commentCount);

            return data;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String extractVideoId(String url) {
        try {
            if (url.contains("v=")) {
                return url.substring(url.indexOf("v=") + 2, url.indexOf("v=") + 13); // standard 11-character ID
            } else if (url.contains("youtu.be/")) {
                return url.substring(url.lastIndexOf("/") + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
