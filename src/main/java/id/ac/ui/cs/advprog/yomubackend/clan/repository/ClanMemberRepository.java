package id.ac.ui.cs.advprog.yomubackend.clan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;


public interface ClanMemberRepository extends JpaRepository<ClanMember, Long> {
    boolean existsByUserId(Long userId);
    boolean existsByClan_IdAndUserId(Long clanId, Long userId);
}