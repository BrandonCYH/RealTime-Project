package com.example.realtimeproject.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YouTubeData {
    private String thumbnailUrl;
    private String videoTitle;
    private String videoUrl;
    private String channelName;
    private long subscriberCount;
    private long totalVideos;
    private long totalLikes;
    private long totalComments;
    private String comments;
}
