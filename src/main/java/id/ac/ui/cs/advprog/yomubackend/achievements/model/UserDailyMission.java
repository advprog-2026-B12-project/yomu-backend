package id.ac.ui.cs.advprog.yomubackend.achievements.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_daily_missions",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "mission_id", "date_assigned"})})
public class UserDailyMission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private DailyMission dailyMission;

    @Column(nullable = false)
    private LocalDate dateAssigned = LocalDate.now();

    @Column(columnDefinition = "integer default 0")
    private Integer currentProgress = 0;

    @Column(columnDefinition = "boolean default false")
    private Boolean isCompleted = false;

    private LocalDateTime completedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}