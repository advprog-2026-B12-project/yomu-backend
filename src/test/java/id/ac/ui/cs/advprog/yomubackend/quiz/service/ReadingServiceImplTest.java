package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.exception.ReadingNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.quiz.ReadingDeletedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReadingServiceImplTest {

    private final ReadingRepository repository = mock(ReadingRepository.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final ReadingServiceImpl service = new ReadingServiceImpl(repository, eventPublisher);

    @Test
    void testCreateReading() {
        Reading reading = new Reading();
        reading.setTitle("Title");

        when(repository.save(reading)).thenReturn(reading);

        Reading result = service.create(reading);

        assertEquals("Title", result.getTitle());
        verify(repository).save(reading);
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(new Reading(), new Reading()));

        List<Reading> readings = service.findAll();

        assertEquals(2, readings.size());
        verify(repository).findAll();
    }

    @Test
    void testUpdateReading() {
        UUID id = UUID.randomUUID();

        Reading existing = new Reading();
        existing.setId(id);
        existing.setTitle("Old");
        existing.setCategory("Old Category");
        existing.setContent("Old Content");

        Reading update = new Reading();
        update.setTitle("New");
        update.setCategory("New Category");
        update.setContent("New Content");

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        Reading result = service.update(id, update);

        assertEquals("New", result.getTitle());
        assertEquals("New Category", result.getCategory());
        assertEquals("New Content", result.getContent());
        verify(repository).save(existing);
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repository).deleteById(id);
        verify(eventPublisher).publishEvent(any(ReadingDeletedEvent.class));
    }

    @Test
    void testDelete_NotFound() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(ReadingNotFoundException.class, () -> service.delete(id));

        verify(repository, never()).deleteById(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();

        Reading reading = new Reading();
        reading.setId(id);
        reading.setTitle("Test Reading");

        when(repository.findById(id)).thenReturn(Optional.of(reading));

        Reading result = service.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Test Reading", result.getTitle());

        verify(repository).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        ReadingNotFoundException exception = assertThrows(
                ReadingNotFoundException.class,
                () -> service.findById(id)
        );

        assertEquals("Reading not found: " + id, exception.getMessage());

        verify(repository).findById(id);
    }
}
