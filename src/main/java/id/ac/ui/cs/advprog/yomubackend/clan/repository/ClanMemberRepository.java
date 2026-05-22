package id.ac.ui.cs.advprog.yomubackend.clan.repository;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClanMemberRepository extends JpaRepository<ClanMember, Long> {
    List<ClanMember> findByClan(Clan clan);
    List<ClanMember> findByClanIn(Collection<Clan> clans);
    Optional<ClanMember> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    long countByClan(Clan clan);
    void deleteByClan(Clan clan);
}