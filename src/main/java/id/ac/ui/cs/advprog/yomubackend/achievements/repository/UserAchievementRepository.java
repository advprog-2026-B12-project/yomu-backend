package id.ac.ui.cs.advprog.yomubackend.achievements.repository;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {
    List<UserAchievement> findByUserId(UUID userId);

    Optional<UserAchievement> findByUserIdAndAchievementId(UUID userId, UUID achievementId);

    List<UserAchievement> findByUserIdAndIsDisplayedTrue(UUID userId);
}