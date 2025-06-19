package com.example.realtimeproject.Controller;

import com.example.realtimeproject.Model.VideoLink;
import com.example.realtimeproject.Model.YouTubeData;
import com.example.realtimeproject.Repository.VideoLinkRepository;
import com.example.realtimeproject.Service.TelegramNotifier;
import com.example.realtimeproject.Service.YouTubeService;
import com.example.realtimeproject.springproducer.SpringProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class VideoLinkController {

    @Autowired
    private VideoLinkRepository videoLinkRepository;

    @Autowired
    private YouTubeService youTubeService;

    @Autowired
    private SpringProducer springProducer;

    @GetMapping("/")
    public String showForm() {
        return "index"; // Loads videoForm.html
    }

    @PostMapping("/submit-link")
    public String handleSubmit(@RequestParam("url") List<String> urls, Model model) {
        List<YouTubeData> videoInfos = new ArrayList<>();
        TelegramNotifier notifier = new TelegramNotifier();
        boolean sentToTelegram = false;

        for (String link : urls) {
            videoLinkRepository.save(new VideoLink(link));
            String videoId = youTubeService.extractVideoId(link);
            if (videoId != null) {
                YouTubeData data = youTubeService.fetchVideoData(videoId);
                if (data != null) {
                    if (data.getComments() != null) {
                        int length = data.getComments().length();
                        System.out.println("Comment length: " + length);

                        if (length % 2 == 0) {
                            // Even: send to Kafka only
                            springProducer.sendMessage(List.of(data)); // Send one item at a time
                            videoInfos.add(data);
                            System.out.println("Data is sent to Kafka: " + data.getVideoTitle());
                            System.out.println("Sending to Kafka: " + data);
                        } else {
                            // Odd: send to Telegram only
                            String formatted = notifier.formatYouTubeData(data);
                            notifier.sendMessageToTelegram(formatted);
                            System.out.println("Data is sent to Telegram");
                            sentToTelegram = true;
                        }
                    }
                }
            }
        }

        model.addAttribute("videoInfos", videoInfos);
        model.addAttribute("sentToTelegram", sentToTelegram);
        return "result";
    }
}