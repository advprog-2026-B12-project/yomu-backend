package id.ac.ui.cs.advprog.yomubackend.repository;

import id.ac.ui.cs.advprog.yomubackend.entity.ClanMember;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ClanMemberRepository extends JpaRepository<ClanMember, Long> {
    boolean existsByUserId(Long userId);
    boolean existsByClan_IdAndUserId(Long clanId, Long userId);
}