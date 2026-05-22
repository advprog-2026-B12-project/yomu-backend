package id.ac.ui.cs.advprog.yomubackend.clan.listener;

import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberQuizStatRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.quiz.ReadingDeletedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadingDeletedEventListenerTest {

    @Mock
    private ClanMemberQuizStatRepository clanMemberQuizStatRepository;

    @InjectMocks
    private ReadingDeletedEventListener listener;

    @Test
    void handleReadingDeleted_DeletesStatsByReadingId() {
        UUID readingId = UUID.randomUUID();
        ReadingDeletedEvent event = new ReadingDeletedEvent(readingId, LocalDateTime.now());

        listener.handleReadingDeleted(event);

        verify(clanMemberQuizStatRepository, times(1)).deleteByReadingId(readingId);
    }
}
