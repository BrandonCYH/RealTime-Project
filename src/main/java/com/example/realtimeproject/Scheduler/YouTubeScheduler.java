package com.example.realtimeproject.Scheduler;

import com.example.realtimeproject.Model.YouTubeLink;
import com.example.realtimeproject.Repository.YouTubeLinkRepository;
import com.example.realtimeproject.Service.YouTubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class YouTubeScheduler {

    @Autowired
    private YouTubeLinkRepository repo;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private int currentIndex = 0; // Track the current index

    @Scheduled(fixedRate = 5000)
    public void fetchDataAndSend() throws IOException {
        List<YouTubeLink> links = repo.findAll();
        if (links.isEmpty()) {
            System.out.println("No YouTube channel found.");
            return;
        }

        // Ensure index wraps around if list size changes or limit reached
        if (currentIndex >= links.size()) {
            currentIndex = 0;
        }

        YouTubeLink link = links.get(currentIndex);
        String json = fetchMockMetadata(link.getUrl());

        String topic = "Youtube-data-channel";
        kafkaTemplate.send(topic, json);
        System.out.println("Sent to Kafka: " + json);

        currentIndex++; // Move to the next channel for the next cycle
    }

    private String fetchMockMetadata(String url) throws IOException {
        // Normally call YouTube API here
        YouTubeService service = new YouTubeService();
        return service.getInfoFromYouTubeLink(url);
    }
}