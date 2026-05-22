package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanLeaderCannotLeaveException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UserNotInClanException;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.auth.UserLeftClanEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ClanMemberServiceImpl implements ClanMemberService {

    private final ClanMemberRepository clanMemberRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ClanMemberServiceImpl(ClanMemberRepository clanMemberRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.clanMemberRepository = clanMemberRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void leaveClan(UUID userId) {
        ClanMember member = clanMemberRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotInClanException("User is not in any clan"));

        if (member.getRole() == ClanMember.Role.LEADER) {
            throw new ClanLeaderCannotLeaveException();
        }

        Long clanId = member.getClan().getId();
        clanMemberRepository.delete(member);
        eventPublisher.publishEvent(new UserLeftClanEvent(userId, clanId, LocalDateTime.now()));
    }
}
