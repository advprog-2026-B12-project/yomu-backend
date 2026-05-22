package id.ac.ui.cs.advprog.yomubackend.clan.listener;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMemberDailyMissionCompletion;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberDailyMissionCompletionRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.achievements.DailyMissionCompletedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyMissionCompletedEventListenerTest {

    @Mock
    private ClanMemberDailyMissionCompletionRepository completionRepository;

    @InjectMocks
    private DailyMissionCompletedEventListener listener;

    @Test
    void handleDailyMissionCompleted_NewEntry_SavesCompletion() {
        UUID userId = UUID.randomUUID();
        LocalDate today = LocalDate.now();
        DailyMissionCompletedEvent event = new DailyMissionCompletedEvent(userId, today);
        when(completionRepository.existsByUserIdAndDateAssigned(userId, today)).thenReturn(false);

        listener.handleDailyMissionCompleted(event);

        ArgumentCaptor<ClanMemberDailyMissionCompletion> captor =
                ArgumentCaptor.forClass(ClanMemberDailyMissionCompletion.class);
        verify(completionRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
        assertThat(captor.getValue().getDateAssigned()).isEqualTo(today);
    }

    @Test
    void handleDailyMissionCompleted_AlreadyExists_DoesNotSaveDuplicate() {
        UUID userId = UUID.randomUUID();
        LocalDate today = LocalDate.now();
        DailyMissionCompletedEvent event = new DailyMissionCompletedEvent(userId, today);
        when(completionRepository.existsByUserIdAndDateAssigned(userId, today)).thenReturn(true);

        listener.handleDailyMissionCompleted(event);

        verify(completionRepository, never()).save(any());
    }
}
