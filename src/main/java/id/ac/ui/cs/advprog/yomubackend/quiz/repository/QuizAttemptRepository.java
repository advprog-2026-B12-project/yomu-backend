package id.ac.ui.cs.advprog.yomubackend.quiz.repository;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
    List<QuizAttempt> findByUserId(UUID userId);

    List<QuizAttempt> findByUserIdAndCreatedAtBetween(
            UUID userId,
            LocalDateTime start,
            LocalDateTime end
    );
}
