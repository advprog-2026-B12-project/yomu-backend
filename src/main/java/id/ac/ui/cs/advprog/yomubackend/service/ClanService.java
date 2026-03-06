package id.ac.ui.cs.advprog.yomubackend.service;

import id.ac.ui.cs.advprog.yomubackend.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomubackend.repository.ClanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClanService {

    private final ClanRepository clanRepository;
    private final ClanMemberRepository clanMemberRepository;

    public ClanService(ClanRepository clanRepository, ClanMemberRepository clanMemberRepository) {
        this.clanRepository = clanRepository;
        this.clanMemberRepository = clanMemberRepository;
    }

    @Transactional
    public Clan createClan(Long userId, String name, String description) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name is required");

        if (clanMemberRepository.existsByUserId(userId)) {
            throw new IllegalStateException("User already in a clan");
        }

        if (clanRepository.existsByNameIgnoreCase(name.trim())) {
            throw new IllegalStateException("Clan name already exists");
        }

        Clan clan = new Clan();
        clan.setName(name.trim());
        clan.setDescription(description);
        clan.setLeaderUserId(userId);

        Clan saved = clanRepository.save(clan);

        ClanMember leader = new ClanMember();
        leader.setClan(saved);
        leader.setUserId(userId);
        leader.setRole(ClanMember.Role.LEADER);
        clanMemberRepository.save(leader);

        return saved;
    }

    @Transactional
    public ClanMember joinClan(Long userId, Long clanId) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (clanId == null) throw new IllegalArgumentException("clanId is required");

        if (clanMemberRepository.existsByUserId(userId)) {
            throw new IllegalStateException("User already in a clan");
        }

        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new IllegalArgumentException("Clan not found"));

        if (clanMemberRepository.existsByClan_IdAndUserId(clanId, userId)) {
            throw new IllegalStateException("Already joined this clan");
        }

        ClanMember member = new ClanMember();
        member.setClan(clan);
        member.setUserId(userId);
        member.setRole(ClanMember.Role.MEMBER);

        return clanMemberRepository.save(member);
    }
}