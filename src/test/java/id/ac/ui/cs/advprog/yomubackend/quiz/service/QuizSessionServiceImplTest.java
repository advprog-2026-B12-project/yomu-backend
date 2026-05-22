package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.exception.QuizAlreadyStartedException;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.QuizSession;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizSessionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuizSessionServiceImplTest {

    private final QuizSessionRepository repository = mock(QuizSessionRepository.class);
    private final QuizSessionServiceImpl service = new QuizSessionServiceImpl(repository);

    @Test
    void start_whenSessionDoesNotExist_savesNewSession() {
        UUID userId = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();

        when(repository.existsByUserIdAndReadingId(userId, readingId)).thenReturn(false);

        service.start(userId, readingId);

        ArgumentCaptor<QuizSession> captor = ArgumentCaptor.forClass(QuizSession.class);
        verify(repository).save(captor.capture());

        QuizSession saved = captor.getValue();
        assertEquals(userId, saved.getUserId());
        assertEquals(readingId, saved.getReadingId());
        assertNotNull(saved.getStartedAt());
    }

    @Test
    void start_whenSessionAlreadyExists_doesNotSaveDuplicate() {
        UUID userId = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();

        when(repository.existsByUserIdAndReadingId(userId, readingId)).thenReturn(true);

        service.start(userId, readingId);

        verify(repository, never()).save(any());
    }

    @Test
    void hasStarted_delegatesToRepository() {
        UUID userId = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();

        when(repository.existsByUserIdAndReadingId(userId, readingId)).thenReturn(true);

        assertTrue(service.hasStarted(userId, readingId));
        verify(repository).existsByUserIdAndReadingId(userId, readingId);
    }

    @Test
    void ensureNotStarted_whenSessionAlreadyExists_throwsException() {
        UUID userId = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();

        when(repository.existsByUserIdAndReadingId(userId, readingId)).thenReturn(true);

        assertThrows(
                QuizAlreadyStartedException.class,
                () -> service.ensureNotStarted(userId, readingId)
        );
    }
}
