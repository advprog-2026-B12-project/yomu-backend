package id.ac.ui.cs.advprog.yomubackend.clan.listener;

import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanJoinRequestRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberDailyMissionCompletionRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberQuizStatRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.auth.UserDeletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanUserDeletedEventListenerTest {

    @Mock
    private ClanMemberRepository clanMemberRepository;

    @Mock
    private ClanJoinRequestRepository clanJoinRequestRepository;

    @Mock
    private ClanMemberQuizStatRepository clanMemberQuizStatRepository;

    @Mock
    private ClanMemberDailyMissionCompletionRepository completionRepository;

    @InjectMocks
    private ClanUserDeletedEventListener listener;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void handleUserDeleted_DeletesAllClanDataForUser() {
        UserDeletedEvent event = new UserDeletedEvent(userId);

        listener.handleUserDeleted(event);

        verify(clanMemberRepository, times(1)).deleteByUserId(userId);
        verify(clanJoinRequestRepository, times(1)).deleteByUserId(userId);
        verify(clanMemberQuizStatRepository, times(1)).deleteByUserId(userId);
        verify(completionRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    void handleUserDeleted_UserHasNoData_StillCallsAllRepositories() {
        UserDeletedEvent event = new UserDeletedEvent(userId);
        doNothing().when(clanMemberRepository).deleteByUserId(userId);
        doNothing().when(clanJoinRequestRepository).deleteByUserId(userId);
        doNothing().when(clanMemberQuizStatRepository).deleteByUserId(userId);
        doNothing().when(completionRepository).deleteByUserId(userId);

        listener.handleUserDeleted(event);

        verify(clanMemberRepository, times(1)).deleteByUserId(userId);
        verify(clanJoinRequestRepository, times(1)).deleteByUserId(userId);
        verify(clanMemberQuizStatRepository, times(1)).deleteByUserId(userId);
        verify(completionRepository, times(1)).deleteByUserId(userId);
    }
}
