package id.ac.ui.cs.advprog.yomubackend.clan.repository;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMemberDailyMissionCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

@Repository
public interface ClanMemberDailyMissionCompletionRepository extends JpaRepository<ClanMemberDailyMissionCompletion, Long> {
    boolean existsByUserIdAndDateAssigned(UUID userId, LocalDate dateAssigned);
    long countByUserIdInAndDateAssigned(Collection<UUID> userIds, LocalDate dateAssigned);
    void deleteByUserId(UUID userId);
}
