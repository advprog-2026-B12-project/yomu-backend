package id.ac.ui.cs.advprog.yomubackend.achievements.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_achievements",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "achievement_id"})})
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column(columnDefinition = "integer default 0")
    private Integer currentProgress = 0;

    @Column(columnDefinition = "boolean default false")
    private Boolean isUnlocked = false;

    @Column(columnDefinition = "boolean default false")
    private Boolean isDisplayed = false;

    private LocalDateTime unlockedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}