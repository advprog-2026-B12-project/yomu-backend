package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.ClanLeaderCannotLeaveException;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UserNotInClanException;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanMemberServiceImplTest {

    private static final UUID LEADER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Mock private ClanMemberRepository clanMemberRepository;

    private ClanMemberService service;
    private Clan clan;

    @BeforeEach
    void setUp() {
        service = new ClanMemberServiceImpl(clanMemberRepository);

        clan = new Clan();
        clan.setId(1L);
        clan.setName("Warriors");
        clan.setLeaderUserId(LEADER_ID);
        clan.setDivision("BRONZE");
    }

    @Test
    void leaveClan_shouldDeleteMember_whenUserIsRegularMember() {
        ClanMember member = new ClanMember();
        member.setUserId(MEMBER_ID);
        member.setRole(ClanMember.Role.MEMBER);
        member.setClan(clan);

        when(clanMemberRepository.findByUserId(MEMBER_ID)).thenReturn(Optional.of(member));

        assertDoesNotThrow(() -> service.leaveClan(MEMBER_ID));

        verify(clanMemberRepository).delete(member);
    }

    @Test
    void leaveClan_shouldThrowUserNotInClanException_whenUserHasNoClan() {
        when(clanMemberRepository.findByUserId(MEMBER_ID)).thenReturn(Optional.empty());

        UserNotInClanException ex = assertThrows(UserNotInClanException.class,
                () -> service.leaveClan(MEMBER_ID));

        assertEquals("User is not in any clan", ex.getMessage());
        verify(clanMemberRepository, never()).delete(any());
    }

    @Test
    void leaveClan_shouldThrowClanLeaderCannotLeaveException_whenUserIsLeader() {
        ClanMember leader = new ClanMember();
        leader.setUserId(LEADER_ID);
        leader.setRole(ClanMember.Role.LEADER);
        leader.setClan(clan);

        when(clanMemberRepository.findByUserId(LEADER_ID)).thenReturn(Optional.of(leader));

        ClanLeaderCannotLeaveException ex = assertThrows(ClanLeaderCannotLeaveException.class,
                () -> service.leaveClan(LEADER_ID));

        assertEquals("Clan leader cannot leave the clan. Delete the clan instead.", ex.getMessage());
        verify(clanMemberRepository, never()).delete(any());
    }
}
