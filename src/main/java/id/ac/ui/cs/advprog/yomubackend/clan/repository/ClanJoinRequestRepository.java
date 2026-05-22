package id.ac.ui.cs.advprog.yomubackend.clan.repository;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClanJoinRequestRepository extends JpaRepository<ClanJoinRequest, Long> {
    boolean existsByUserIdAndStatus(UUID userId, ClanJoinRequest.Status status);
    List<ClanJoinRequest> findByClanAndStatus(Clan clan, ClanJoinRequest.Status status);
    List<ClanJoinRequest> findByUserIdAndStatus(UUID userId, ClanJoinRequest.Status status);
    void deleteByClan(Clan clan);
}
