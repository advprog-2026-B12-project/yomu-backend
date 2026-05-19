package id.ac.ui.cs.advprog.yomubackend.quiz.repository;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.ReadingProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, UUID> {
    boolean existsByUserIdAndReadingId(UUID userId, UUID readingId);
    void deleteByReadingId(UUID readingId);
}
