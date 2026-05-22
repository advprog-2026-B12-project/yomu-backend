package id.ac.ui.cs.advprog.yomubackend.clan.repository;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMemberQuizStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClanMemberQuizStatRepository extends JpaRepository<ClanMemberQuizStat, Long> {
    List<ClanMemberQuizStat> findByUserId(UUID userId);
    List<ClanMemberQuizStat> findByUserIdInAndCompletedAtBetween(Collection<UUID> userIds, LocalDateTime start, LocalDateTime end);
    void deleteByReadingId(UUID readingId);
    void deleteByUserId(UUID userId);
}
