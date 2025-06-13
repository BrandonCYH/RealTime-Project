package com.example.realtimeproject.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class YouTubeBot extends TelegramLongPollingBot {
    private static final String COMMAND_CHANNEL = "/channel";
    private static final String COMMAND_START = "/start";

    private final ChannelCommandHandler channelCommandHandler = new ChannelCommandHandler();

    @Override
    public String getBotUsername() {
        return "s301051_bot";
    }

    @Override
    public String getBotToken() {
        return "7431892081:AAHw0f3OFwKTAR0PB70SmGe_HQN-hR7JIHg";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        Message message = update.getMessage();
        String text = message.getText().trim();

        if (text.startsWith(COMMAND_START)) {
            handleStartCommand(message);
        } else if (text.startsWith(COMMAND_CHANNEL)) {
            handleChannelCommand(message);
        } else {
            sendReply(message.getChatId(), "ℹ️ Available commands:\n/start\n/channel <username or Channel ID>");
        }
    }

    private void handleStartCommand(Message message) {
        String welcomeMessage = "👋 Welcome to YTAnalytica!\n" +
                "I can help you analyze YouTube channel data.\n" +
                "Use the following commands to get started:\n" +
                "▶️ /channel <username or Channel ID> - Get channel statistics\n" +
                "\nHappy exploring!";
        sendReply(message.getChatId(), welcomeMessage);
    }

    private void handleChannelCommand(Message message) {
        String[] parts = message.getText().trim().split(" ", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            sendReply(message.getChatId(), "❗ Incorrect format: /channel <username or Channel ID>");
            return;
        }
        String argument = parts[1].trim();
        try {
            String response = channelCommandHandler.handleCommand(argument);
            sendReply(message.getChatId(), response);
        } catch (Exception ex) {
            ex.printStackTrace();
            sendReply(message.getChatId(), "❌ Error executing the command: " + ex.getMessage());
        }
    }


    private void sendReply(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.err.println("Failed to send message: " + e.getMessage()); // Log the error
        }
    }
}
