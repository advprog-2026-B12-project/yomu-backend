package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanJoinRequestResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanMemberResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanJoinRequest;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanJoinRequestNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.JoinRequestAlreadyResolvedException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.PendingJoinRequestAlreadyExistsException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UnauthorizedClanActionException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UserAlreadyInClanException;
import id.ac.ui.cs.advprog.yomubackend.clan.mapper.ClanMapper;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanJoinRequestRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ClanJoinRequestServiceImpl implements ClanJoinRequestService {

    private final ClanRepository clanRepository;
    private final ClanMemberRepository clanMemberRepository;
    private final ClanJoinRequestRepository clanJoinRequestRepository;
    private final ClanMapper clanMapper;

    public ClanJoinRequestServiceImpl(ClanRepository clanRepository,
                                      ClanMemberRepository clanMemberRepository,
                                      ClanJoinRequestRepository clanJoinRequestRepository,
                                      ClanMapper clanMapper) {
        this.clanRepository = clanRepository;
        this.clanMemberRepository = clanMemberRepository;
        this.clanJoinRequestRepository = clanJoinRequestRepository;
        this.clanMapper = clanMapper;
    }

    @Override
    @Transactional
    public ClanJoinRequestResponse requestToJoinClan(UUID userId, Long clanId) {
        Clan clan = findClanById(clanId);
        ensureUserHasNoClan(userId);
        ensureUserHasNoPendingJoinRequest(userId);

        ClanJoinRequest request = new ClanJoinRequest();
        request.setClan(clan);
        request.setUserId(userId);
        request.setStatus(ClanJoinRequest.Status.PENDING);

        ClanJoinRequest savedRequest = clanJoinRequestRepository.save(request);
        return clanMapper.toJoinRequestResponse(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClanJoinRequestResponse> getPendingJoinRequests(UUID requesterUserId, Long clanId) {
        Clan clan = findClanById(clanId);
        ensureRequesterIsClanLeader(requesterUserId, clan);

        return clanJoinRequestRepository.findByClanAndStatus(clan, ClanJoinRequest.Status.PENDING)
                .stream()
                .map(clanMapper::toJoinRequestResponse)
                .toList();
    }

    @Override
    @Transactional
    public ClanMemberResponse approveJoinRequest(UUID requesterUserId, Long requestId) {
        ClanJoinRequest request = findJoinRequestById(requestId);
        ensureRequesterIsClanLeader(requesterUserId, request.getClan());
        ensureJoinRequestIsPending(request);
        ensureUserHasNoClan(request.getUserId());

        ClanMember member = new ClanMember();
        member.setClan(request.getClan());
        member.setUserId(request.getUserId());
        member.setRole(ClanMember.Role.MEMBER);

        ClanMember savedMember = clanMemberRepository.save(member);
        resolveJoinRequest(request, ClanJoinRequest.Status.APPROVED);
        rejectOtherPendingRequests(request);

        return clanMapper.toMemberResponse(savedMember);
    }

    @Override
    @Transactional
    public void rejectJoinRequest(UUID requesterUserId, Long requestId) {
        ClanJoinRequest request = findJoinRequestById(requestId);
        ensureRequesterIsClanLeader(requesterUserId, request.getClan());
        ensureJoinRequestIsPending(request);
        resolveJoinRequest(request, ClanJoinRequest.Status.REJECTED);
    }

    private void ensureUserHasNoClan(UUID userId) {
        if (clanMemberRepository.existsByUserId(userId)) {
            throw new UserAlreadyInClanException("User is already in a clan");
        }
    }

    private void ensureUserHasNoPendingJoinRequest(UUID userId) {
        if (clanJoinRequestRepository.existsByUserIdAndStatus(userId, ClanJoinRequest.Status.PENDING)) {
            throw new PendingJoinRequestAlreadyExistsException();
        }
    }

    private void ensureRequesterIsClanLeader(UUID requesterUserId, Clan clan) {
        if (!clan.getLeaderUserId().equals(requesterUserId)) {
            throw new UnauthorizedClanActionException("Only the clan leader can manage join requests");
        }
    }

    private void ensureJoinRequestIsPending(ClanJoinRequest request) {
        if (request.getStatus() != ClanJoinRequest.Status.PENDING) {
            throw new JoinRequestAlreadyResolvedException();
        }
    }

    private Clan findClanById(Long clanId) {
        return clanRepository.findById(clanId)
                .orElseThrow(() -> new ClanNotFoundException("Clan not found"));
    }

    private ClanJoinRequest findJoinRequestById(Long requestId) {
        return clanJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ClanJoinRequestNotFoundException("Clan join request not found"));
    }

    private void resolveJoinRequest(ClanJoinRequest request, ClanJoinRequest.Status status) {
        request.setStatus(status);
        request.setResolvedAt(Instant.now());
        clanJoinRequestRepository.save(request);
    }

    private void rejectOtherPendingRequests(ClanJoinRequest approvedRequest) {
        List<ClanJoinRequest> otherPendingRequests = clanJoinRequestRepository
                .findByUserIdAndStatus(approvedRequest.getUserId(), ClanJoinRequest.Status.PENDING)
                .stream()
                .filter(request -> !request.getId().equals(approvedRequest.getId()))
                .toList();

        otherPendingRequests.forEach(request -> {
            request.setStatus(ClanJoinRequest.Status.REJECTED);
            request.setResolvedAt(Instant.now());
        });

        clanJoinRequestRepository.saveAll(otherPendingRequests);
    }
}
