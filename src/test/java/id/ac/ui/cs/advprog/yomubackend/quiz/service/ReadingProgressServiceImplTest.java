package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.exception.ReadingNotOpenedException;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.ReadingProgress;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingProgressRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReadingProgressServiceImplTest {

    private final ReadingProgressRepository repository = mock(ReadingProgressRepository.class);
    private final ReadingProgressServiceImpl service = new ReadingProgressServiceImpl(repository);

    @Test
    void markOpened_whenProgressDoesNotExist_savesNewProgress() {
        UUID userId = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();

        when(repository.existsByUserIdAndReadingId(userId, readingId)).thenReturn(false);

        service.markOpened(userId, readingId);

        ArgumentCaptor<ReadingProgress> captor = ArgumentCaptor.forClass(ReadingProgress.class);
        verify(repository).save(captor.capture());
        assertEquals(userId, captor.getValue().getUserId());
        assertEquals(readingId, captor.getValue().getReadingId());
        assertNotNull(captor.getValue().getOpenedAt());
    }

    @Test
    void markOpened_whenProgressExists_doesNotSaveDuplicate() {
        UUID userId = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();

        when(repository.existsByUserIdAndReadingId(userId, readingId)).thenReturn(true);

        service.markOpened(userId, readingId);

        verify(repository, never()).save(any());
    }

    @Test
    void ensureOpened_whenProgressExists_doesNotThrow() {
        UUID userId = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();

        when(repository.existsByUserIdAndReadingId(userId, readingId)).thenReturn(true);

        assertDoesNotThrow(() -> service.ensureOpened(userId, readingId));
    }

    @Test
    void ensureOpened_whenProgressDoesNotExist_throwsException() {
        UUID userId = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();

        when(repository.existsByUserIdAndReadingId(userId, readingId)).thenReturn(false);

        ReadingNotOpenedException exception = assertThrows(
                ReadingNotOpenedException.class,
                () -> service.ensureOpened(userId, readingId)
        );
        assertEquals("Reading must be opened before starting quiz", exception.getMessage());
    }
}
