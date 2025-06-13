package com.example.realtimeproject.telegrambot;

import org.json.JSONObject;

public class ChannelCommandHandler {
    private final YouTubeService youTubeService = new YouTubeService();
    KafkaBotProducer kafkaProducer = new KafkaBotProducer();

    public String handleCommand(String argument) {
        try {
            String channelId;
            if (argument.startsWith("UC")) {
                channelId = argument;
            } else {
                channelId = youTubeService.getChannelIdFromUsername(argument);
            }
            if (channelId == null) {
                return "❌ Could not find a channel with the provided username. Please verify the input.";
            }

            String info = youTubeService.fetchChannelInfo(channelId);
            String growth = youTubeService.getChannelGrowth(channelId);
            String frequency = youTubeService.getUploadFrequency(channelId);
            String youtubeLink = "👉 YouTube Channel: https://www.youtube.com/channel/" + channelId;

            // Format final message for display
            String resultMessage = String.join("\n\n",
                    info,
                    growth,
                    frequency,
                    youtubeLink
            );

            // Create JSON with the full formatted message
            JSONObject json = new JSONObject();
            json.put("channelId", channelId);
            json.put("formattedMessage", resultMessage);  // <- the only field Consumer needs

            kafkaProducer.sendMessage(json.toString());

            return resultMessage;

        } catch (Exception ex) {
            ex.printStackTrace();
            return "❌ An error occurred while processing the command: " + ex.getMessage();
        }
    }
}
