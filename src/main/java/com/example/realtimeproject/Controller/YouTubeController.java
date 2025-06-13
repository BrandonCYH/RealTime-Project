package com.example.realtimeproject.Controller;

import com.example.realtimeproject.Model.YouTubeLink;
import com.example.realtimeproject.Repository.YouTubeLinkRepository;
import com.example.realtimeproject.Service.YouTubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class YouTubeController {

    @Autowired
    private YouTubeLinkRepository repository;

    @PostMapping("/submit")
    @ResponseBody
    public String submitLinks(@RequestParam("urls") List<String> urls) throws IOException {
        StringBuilder result = new StringBuilder();
        YouTubeService youTubeService = new YouTubeService();

        for (String url : urls) {
            if (url != null && !url.trim().isEmpty()) {
                String trimmedUrl = url.trim();

                YouTubeLink link = new YouTubeLink();
                link.setUrl(trimmedUrl);
                repository.save(link);

                String info = youTubeService.getInfoFromYouTubeLink(trimmedUrl);
                result.append("Info for ").append(trimmedUrl).append(":\n").append(info).append("\n\n");
            }
        }

        return result.toString();
    }

    @GetMapping("/all")
    public List<YouTubeLink> getAll() {
        return repository.findAll();
    }
}
