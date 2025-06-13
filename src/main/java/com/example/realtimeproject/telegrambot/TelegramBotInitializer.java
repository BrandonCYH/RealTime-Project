package com.example.realtimeproject.telegrambot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramBotInitializer {
    public static void initializeBot() throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new YouTubeBot());
        System.out.println("Bot started successfully...");
    }
}
