package com.example.realtimeproject.Controller;

public class YouTubeApiConstants {
    public static final String API_KEY = "AIzaSyCLB7dWVJfFbem7V_9ljI2MS3IKH0cm3wU";
    public static final String CHANNEL_URL = "https://www.googleapis.com/youtube/v3/channels?part=statistics,snippet&id=%s&key=" + API_KEY;
    public static final String SEARCH_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=channel&q=%s&key=" + API_KEY;
    public static final String VIDEOS_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=%s&maxResults=2&order=date&key=" + API_KEY;
}
