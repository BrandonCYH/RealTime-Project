package com.example.realtimeproject.telegrambot;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeFormatter {
    public static String formatTimeDifference(Instant earlier, Instant later) {
        long hours = ChronoUnit.HOURS.between(earlier, later);
        if (hours < 24) {
            return String.format("%d hour%s", hours, (hours != 1 ? "s" : ""));
        } else {
            long days = ChronoUnit.DAYS.between(earlier, later);
            return String.format("%d day%s", days, (days != 1 ? "s" : ""));
        }
    }

    public static String formatElapsedTime(Instant past) {
        Instant now = Instant.now();
        long hours = ChronoUnit.HOURS.between(past, now);
        if (hours < 24) {
            return String.format("%d hour%s ago", hours, (hours != 1 ? "s" : ""));
        } else {
            long days = ChronoUnit.DAYS.between(past, now);
            return String.format("%d day%s ago", days, (days != 1 ? "s" : ""));
        }
    }
}
