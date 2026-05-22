package id.ac.ui.cs.advprog.yomubackend.discussion.listener;

import id.ac.ui.cs.advprog.yomubackend.discussion.repository.CommentRepository;
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
    private CommentRepository commentRepository;

    @InjectMocks
    private ReadingDeletedEventListener listener;

    @Test
    void handleReadingDeleted_DeletesAllCommentsForReading() {
        UUID readingId = UUID.randomUUID();
        ReadingDeletedEvent event = new ReadingDeletedEvent(readingId, LocalDateTime.now());

        listener.handleReadingDeleted(event);

        verify(commentRepository, times(1)).deleteAllByReadingId(readingId);
    }

    @Test
    void handleReadingDeleted_NoCommentsExist_StillCallsRepository() {
        UUID readingId = UUID.randomUUID();
        ReadingDeletedEvent event = new ReadingDeletedEvent(readingId, LocalDateTime.now());
        doNothing().when(commentRepository).deleteAllByReadingId(readingId);

        listener.handleReadingDeleted(event);

        verify(commentRepository, times(1)).deleteAllByReadingId(readingId);
    }
}
