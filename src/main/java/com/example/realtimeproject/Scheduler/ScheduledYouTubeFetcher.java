package com.example.realtimeproject.Scheduler;

import com.example.realtimeproject.Model.VideoLink;
import com.example.realtimeproject.Model.YouTubeData;
import com.example.realtimeproject.Repository.VideoLinkRepository;
import com.example.realtimeproject.Service.YouTubeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledYouTubeFetcher {

    @Autowired
    private VideoLinkRepository videoLinkRepository;

    @Autowired
    private YouTubeService youTubeService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 5000)
    public void fetchAndPublish() throws JsonProcessingException {
        List<VideoLink> links = videoLinkRepository.findByProcessedFalse();

        for (VideoLink link : links) {
            String videoId = extractVideoId(link.getUrl());
            YouTubeData data = youTubeService.fetchVideoData(videoId);

            String json = new ObjectMapper().writeValueAsString(data); // Convert to JSON
            kafkaTemplate.send("video-message", json);

            link.setProcessed(true);
            videoLinkRepository.save(link);
        }
    }

    private String extractVideoId(String url) {
        try {
            if (url.contains("v=")) {
                return url.split("v=")[1].split("&")[0];
            } else if (url.contains("youtu.be/")) {
                return url.split("youtu.be/")[1].split("\\?")[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

