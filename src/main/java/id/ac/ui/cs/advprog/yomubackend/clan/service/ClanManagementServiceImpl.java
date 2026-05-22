package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanMemberResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanNameAlreadyTakenException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanNameBlankException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UnauthorizedClanActionException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UserAlreadyInClanException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UserNotInClanException;
import id.ac.ui.cs.advprog.yomubackend.clan.league.LeagueDivision;
import id.ac.ui.cs.advprog.yomubackend.clan.mapper.ClanMapper;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanJoinRequestRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.clan.ClanDeletedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ClanManagementServiceImpl implements ClanManagementService {

    private final ClanRepository clanRepository;
    private final ClanMemberRepository clanMemberRepository;
    private final ClanJoinRequestRepository clanJoinRequestRepository;
    private final ClanMapper clanMapper;
    private final ApplicationEventPublisher eventPublisher;

    public ClanManagementServiceImpl(ClanRepository clanRepository,
                                     ClanMemberRepository clanMemberRepository,
                                     ClanJoinRequestRepository clanJoinRequestRepository,
                                     ClanMapper clanMapper,
                                     ApplicationEventPublisher eventPublisher) {
        this.clanRepository = clanRepository;
        this.clanMemberRepository = clanMemberRepository;
        this.clanJoinRequestRepository = clanJoinRequestRepository;
        this.clanMapper = clanMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public ClanResponse createClan(UUID userId, String name, String description) {
        validateClanName(name);
        ensureClanNameAvailable(name);
        ensureUserHasNoClan(userId);

        Clan clan = new Clan();
        clan.setName(name);
        clan.setDescription(description);
        clan.setLeaderUserId(userId);
        clan.setDivision(LeagueDivision.BRONZE.value());

        Clan savedClan = clanRepository.save(clan);
        saveLeaderMembership(userId, savedClan);

        return toClanResponse(savedClan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClanResponse> getAllClans() {
        return clanRepository.findAll()
                .stream()
                .map(this::toClanResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClanResponse getClanById(Long clanId) {
        return toClanResponse(findClanById(clanId));
    }

    @Override
    @Transactional(readOnly = true)
    public ClanResponse getMyClan(UUID userId) {
        ClanMember membership = clanMemberRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotInClanException("User is not in any clan"));
        return toClanResponse(membership.getClan());
    }

    @Override
    @Transactional
    public void deleteClan(UUID requesterUserId, Long clanId) {
        Clan clan = findClanById(clanId);

        if (!clan.getLeaderUserId().equals(requesterUserId)) {
            throw new UnauthorizedClanActionException("Only the clan leader can delete the clan");
        }

        clanMemberRepository.deleteByClan(clan);
        clanJoinRequestRepository.deleteByClan(clan);
        clanRepository.delete(clan);
        eventPublisher.publishEvent(new ClanDeletedEvent(clan.getId(), clan.getLeaderUserId(), LocalDateTime.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClanMemberResponse> getMembers(Long clanId) {
        Clan clan = findClanById(clanId);

        return clanMemberRepository.findByClan(clan)
                .stream()
                .map(clanMapper::toMemberResponse)
                .toList();
    }

    private void validateClanName(String name) {
        if (name == null || name.isBlank()) {
            throw new ClanNameBlankException();
        }
    }

    private void ensureClanNameAvailable(String name) {
        if (clanRepository.existsByName(name)) {
            throw new ClanNameAlreadyTakenException(name);
        }
    }

    private void ensureUserHasNoClan(UUID userId) {
        if (clanMemberRepository.existsByUserId(userId)) {
            throw new UserAlreadyInClanException("User is already in a clan");
        }
    }

    private void saveLeaderMembership(UUID userId, Clan clan) {
        ClanMember leader = new ClanMember();
        leader.setClan(clan);
        leader.setUserId(userId);
        leader.setRole(ClanMember.Role.LEADER);
        clanMemberRepository.save(leader);
    }

    private Clan findClanById(Long clanId) {
        return clanRepository.findById(clanId)
                .orElseThrow(() -> new ClanNotFoundException("Clan not found"));
    }

    private ClanResponse toClanResponse(Clan clan) {
        long memberCount = clanMemberRepository.countByClan(clan);
        return clanMapper.toClanResponse(clan, memberCount);
    }
}
