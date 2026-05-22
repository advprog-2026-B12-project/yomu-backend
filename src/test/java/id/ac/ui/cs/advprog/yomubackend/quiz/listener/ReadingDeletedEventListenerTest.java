package id.ac.ui.cs.advprog.yomubackend.quiz.listener;

import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizSessionRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingProgressRepository;
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
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private ReadingProgressRepository readingProgressRepository;

    @Mock
    private QuizSessionRepository quizSessionRepository;

    @InjectMocks
    private ReadingDeletedEventListener listener;

    @Test
    void handleReadingDeleted_DeletesAllQuizDataForReading() {
        UUID readingId = UUID.randomUUID();
        ReadingDeletedEvent event = new ReadingDeletedEvent(readingId, LocalDateTime.now());

        listener.handleReadingDeleted(event);

        verify(quizAttemptRepository, times(1)).deleteByReadingId(readingId);
        verify(readingProgressRepository, times(1)).deleteByReadingId(readingId);
        verify(quizSessionRepository, times(1)).deleteByReadingId(readingId);
    }

    @Test
    void handleReadingDeleted_NoDataExists_StillCallsAllRepositories() {
        UUID readingId = UUID.randomUUID();
        ReadingDeletedEvent event = new ReadingDeletedEvent(readingId, LocalDateTime.now());
        doNothing().when(quizAttemptRepository).deleteByReadingId(readingId);
        doNothing().when(readingProgressRepository).deleteByReadingId(readingId);
        doNothing().when(quizSessionRepository).deleteByReadingId(readingId);

        listener.handleReadingDeleted(event);

        verify(quizAttemptRepository, times(1)).deleteByReadingId(readingId);
        verify(readingProgressRepository, times(1)).deleteByReadingId(readingId);
        verify(quizSessionRepository, times(1)).deleteByReadingId(readingId);
    }
}
