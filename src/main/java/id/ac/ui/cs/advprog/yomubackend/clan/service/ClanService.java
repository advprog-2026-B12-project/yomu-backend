package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanMemberResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UnauthorizedClanActionException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UserAlreadyInClanException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UserNotInClanException;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;

@Service
public class ClanService {

    private final ClanRepository clanRepository;
    private final ClanMemberRepository clanMemberRepository;

    public ClanService(ClanRepository clanRepository, ClanMemberRepository clanMemberRepository) {
        this.clanRepository = clanRepository;
        this.clanMemberRepository = clanMemberRepository;
    }

    @Transactional
    public ClanResponse createClan(UUID userId, String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Clan name must not be blank");
        }

        if (clanRepository.existsByName(name)) {
            throw new IllegalArgumentException("Clan name already exists");
        }

        if (clanMemberRepository.existsByUserId(userId)) {
            throw new UserAlreadyInClanException("User is already in a clan");
        }

        Clan clan = new Clan();
        clan.setName(name);
        clan.setDescription(description);
        clan.setLeaderUserId(userId);
        clan.setDivision("BRONZE");

        Clan savedClan = clanRepository.save(clan);

        ClanMember leader = new ClanMember();
        leader.setClan(savedClan);
        leader.setUserId(userId);
        leader.setRole(ClanMember.Role.LEADER);
        clanMemberRepository.save(leader);

        return toClanResponse(savedClan);
    }

    @Transactional
    public ClanMemberResponse joinClan(UUID userId, Long clanId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new ClanNotFoundException("Clan not found"));

        if (clanMemberRepository.existsByUserId(userId)) {
            throw new UserAlreadyInClanException("User is already in a clan");
        }

        ClanMember member = new ClanMember();
        member.setClan(clan);
        member.setUserId(userId);
        member.setRole(ClanMember.Role.MEMBER);

        ClanMember savedMember = clanMemberRepository.save(member);

        return new ClanMemberResponse(
                savedMember.getUserId(),
                savedMember.getRole(),
                savedMember.getJoinedAt()
        );
    }

    @Transactional
    public void leaveClan(UUID userId) {
        ClanMember member = clanMemberRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotInClanException("User is not in any clan"));

        if (member.getRole() == ClanMember.Role.LEADER) {
            throw new IllegalArgumentException("Clan leader cannot leave the clan. Delete the clan instead.");
        }

        clanMemberRepository.delete(member);
    }

    @Transactional
    public void deleteClan(UUID requesterUserId, Long clanId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new ClanNotFoundException("Clan not found"));

        if (!clan.getLeaderUserId().equals(requesterUserId)) {
            throw new UnauthorizedClanActionException("Only the clan leader can delete the clan");
        }

        clanMemberRepository.deleteByClan(clan);
        clanRepository.delete(clan);
    }

    @Transactional(readOnly = true)
    public List<ClanResponse> getAllClans() {
        return clanRepository.findAll()
                .stream()
                .map(this::toClanResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClanResponse getClanById(Long clanId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new ClanNotFoundException("Clan not found"));
        return toClanResponse(clan);
    }

    @Transactional(readOnly = true)
    public List<ClanMemberResponse> getMembers(Long clanId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new ClanNotFoundException("Clan not found"));

        return clanMemberRepository.findByClan(clan)
                .stream()
                .map(member -> new ClanMemberResponse(
                        member.getUserId(),
                        member.getRole(),
                        member.getJoinedAt()
                ))
                .toList();
    }

    private ClanResponse toClanResponse(Clan clan) {
        long memberCount = clanMemberRepository.countByClan(clan);

        return new ClanResponse(
                clan.getId(),
                clan.getName(),
                clan.getDescription(),
                clan.getLeaderUserId(),
                clan.getDivision(),
                memberCount,
                clan.getCreatedAt()
        );
    }
}