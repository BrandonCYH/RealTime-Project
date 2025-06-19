package com.example.realtimeproject.springproducer;

import com.example.realtimeproject.Model.YouTubeData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpringProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${kafka.topic.name:video-message}") // Default to "telegram-message"
    private String topic;

    public void sendMessage(List<YouTubeData> data) {
        try {
            for (YouTubeData item : data) {
                String json = objectMapper.writeValueAsString(item);
                kafkaTemplate.send(topic, json);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}