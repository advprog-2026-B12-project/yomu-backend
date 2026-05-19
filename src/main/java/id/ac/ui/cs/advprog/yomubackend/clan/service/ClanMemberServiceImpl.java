package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanLeaderCannotLeaveException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UserNotInClanException;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ClanMemberServiceImpl implements ClanMemberService {

    private final ClanMemberRepository clanMemberRepository;

    public ClanMemberServiceImpl(ClanMemberRepository clanMemberRepository) {
        this.clanMemberRepository = clanMemberRepository;
    }

    @Override
    @Transactional
    public void leaveClan(UUID userId) {
        ClanMember member = clanMemberRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotInClanException("User is not in any clan"));

        if (member.getRole() == ClanMember.Role.LEADER) {
            throw new ClanLeaderCannotLeaveException();
        }

        clanMemberRepository.delete(member);
    }
}
