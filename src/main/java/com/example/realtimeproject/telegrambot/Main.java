package com.example.realtimeproject.telegrambot;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotInitializer.initializeBot();
        } catch (Exception e) {
            System.err.println("Bot failed to start: " + e.getMessage());
        }
    }
}
