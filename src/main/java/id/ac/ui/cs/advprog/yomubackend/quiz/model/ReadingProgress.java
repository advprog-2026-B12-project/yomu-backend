package id.ac.ui.cs.advprog.yomubackend.quiz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(
        name = "reading_progress",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reading_progress_user_reading",
                columnNames = {"user_id", "reading_id"}
        )
)
public class ReadingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID readingId;

    @Column(nullable = false)
    private LocalDateTime openedAt;
}
