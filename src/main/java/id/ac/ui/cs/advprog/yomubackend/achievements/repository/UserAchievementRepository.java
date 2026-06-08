package id.ac.ui.cs.advprog.yomubackend.achievements.repository;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {
    @EntityGraph(attributePaths = {"achievement"})
    List<UserAchievement> findByUserId(UUID userId);

    @EntityGraph(attributePaths = {"achievement"})
    Optional<UserAchievement> findByUserIdAndAchievementId(UUID userId, UUID achievementId);

    @EntityGraph(attributePaths = {"achievement"})
    List<UserAchievement> findByUserIdAndIsDisplayedTrue(UUID userId);
    void deleteByUserId(UUID userId);
    void deleteByAchievementId(UUID achievementId);
}