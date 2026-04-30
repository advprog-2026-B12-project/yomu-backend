package id.ac.ui.cs.advprog.yomubackend.achievements.constant;

import java.util.Set;

public final class AchievementEvent {
    private AchievementEvent() {}

    public static final String READING_COMPLETED = "READING_COMPLETED";
    public static final String QUIZ_FINISHED = "QUIZ_FINISHED";
    public static final String PERFECT_QUIZ_SCORE = "PERFECT_QUIZ_SCORE";
    public static final String CLAN_PROMOTION = "CLAN_PROMOTION";
    public static final String LOGIN_STREAK = "LOGIN_STREAK";

    private static final Set<String> SUPPORTED_EVENTS = Set.of(
            READING_COMPLETED,
            QUIZ_FINISHED,
            PERFECT_QUIZ_SCORE,
            CLAN_PROMOTION,
            LOGIN_STREAK
    );

    public static boolean isSupported(String eventType) {
        return eventType != null && SUPPORTED_EVENTS.contains(eventType.trim().toUpperCase());
    }

    public static String normalize(String eventType) {
        return eventType == null ? null : eventType.trim().toUpperCase();
    }

    public static Set<String> supportedEvents() {
        return SUPPORTED_EVENTS;
    }
}