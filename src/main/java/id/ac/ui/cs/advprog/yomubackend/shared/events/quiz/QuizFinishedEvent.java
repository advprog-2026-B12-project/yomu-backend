package id.ac.ui.cs.advprog.yomubackend.shared.events.quiz;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuizFinishedEvent(
        UUID userId,
        UUID readingId,
        int score,
        int total,
        boolean isPerfectScore,
        LocalDateTime timestamp
) {
}