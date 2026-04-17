package id.ac.ui.cs.advprog.yomubackend.achievements.repository;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDailyMissionRepository extends JpaRepository<UserDailyMission, UUID> {
    List<UserDailyMission> findByUserIdAndDateAssigned(UUID userId, LocalDate dateAssigned);

    Optional<UserDailyMission> findByUserIdAndDailyMissionIdAndDateAssigned(UUID userId, UUID missionId, LocalDate dateAssigned);

    List<UserDailyMission> findByUserId(UUID userId);
}