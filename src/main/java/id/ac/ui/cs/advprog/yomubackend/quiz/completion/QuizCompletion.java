package id.ac.ui.cs.advprog.yomubackend.quiz.completion;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuizCompletion(
        UUID userId,
        UUID readingId,
        int score,
        int total,
        LocalDateTime completedAt
) {
    public boolean isPerfectScore() {
        return total > 0 && score == total;
    }
}
