package id.ac.ui.cs.advprog.yomubackend.quiz.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.QuizAttempt;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
    List<QuizAttempt> findByUserId(UUID userId);

    boolean existsByUserIdAndReadingId(UUID userId, UUID readingId);

    List<QuizAttempt> findByUserIdAndCreatedAtBetween(
            UUID userId,
            LocalDateTime start,
            LocalDateTime end
    );
}
