package id.ac.ui.cs.advprog.yomubackend.clan.listener;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMemberQuizStat;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberQuizStatRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.quiz.QuizFinishedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizFinishedEventListenerTest {

    @Mock
    private ClanMemberQuizStatRepository clanMemberQuizStatRepository;

    @InjectMocks
    private QuizFinishedEventListener listener;

    @Test
    void handleQuizFinished_SavesStatWithCorrectFields() {
        UUID userId = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        QuizFinishedEvent event = new QuizFinishedEvent(userId, readingId, 8, 10, false, now);

        listener.handleQuizFinished(event);

        ArgumentCaptor<ClanMemberQuizStat> captor = ArgumentCaptor.forClass(ClanMemberQuizStat.class);
        verify(clanMemberQuizStatRepository, times(1)).save(captor.capture());
        ClanMemberQuizStat saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getReadingId()).isEqualTo(readingId);
        assertThat(saved.getScore()).isEqualTo(8);
        assertThat(saved.getTotal()).isEqualTo(10);
        assertThat(saved.getCompletedAt()).isEqualTo(now);
    }

    @Test
    void handleQuizFinished_PerfectScore_SavesStat() {
        UUID userId = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();
        QuizFinishedEvent event = new QuizFinishedEvent(userId, readingId, 10, 10, true, LocalDateTime.now());

        listener.handleQuizFinished(event);

        verify(clanMemberQuizStatRepository, times(1)).save(any(ClanMemberQuizStat.class));
    }
}
