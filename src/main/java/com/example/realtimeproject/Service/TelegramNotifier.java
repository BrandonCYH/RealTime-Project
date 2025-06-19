package com.example.realtimeproject.Service;

import com.example.realtimeproject.Model.YouTubeData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramNotifier extends TelegramLongPollingBot {

    private final String BOT_USERNAME = "s301051_bot";
    private final String BOT_TOKEN = "7431892081:AAHw0f3OFwKTAR0PB70SmGe_HQN-hR7JIHg";
    private final String CHAT_ID = "5799055100";

    public void sendMessageToTelegram(String text) {
        SendMessage message = new SendMessage();
        message.setChatId(CHAT_ID);
        message.setText(text);

        try {
            execute(message);
            System.out.println("✅ Sent to Telegram");
        } catch (TelegramApiException e) {
            System.err.println("❌ Failed to send to Telegram: " + e.getMessage());
        }
    }

    public String formatYouTubeData(YouTubeData data) {
        return "**YouTube Video Details**\n"
                + "📹 *Title:* " + data.getVideoTitle() + "\n"
                + "📝 *Channel Name:* " + data.getChannelName() + "\n"
                + "💬 *Highest Subscribers:* " + data.getSubscriberCount() + "\n"
                + "📺 *Highest Videos: * " + data.getTotalVideos() + "\n"
                + "💬 *Highest Comments: * " + data.getTotalComments() + "\n"
                + "👍 *Highest Likes: * " + data.getTotalLikes() + "\n"
                + "\n🔗 *Link:* https://www.youtube.com/watch?v=" + data.getVideoUrl()
                ;
    }


    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

    }
}