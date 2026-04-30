package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReadingServiceImplTest {

    private final ReadingRepository repository = mock(ReadingRepository.class);
    private final ReadingServiceImpl service = new ReadingServiceImpl(repository);

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
    void testDelete() {
        UUID id = UUID.randomUUID();

        service.delete(id);

        verify(repository).deleteById(id);
    }

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();

        Reading reading = new Reading();
        reading.setId(id);
        reading.setTitle("Test Reading");

        when(repository.findById(id)).thenReturn(java.util.Optional.of(reading));

        Reading result = service.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Test Reading", result.getTitle());

        verify(repository).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.findById(id)
        );

        assertEquals("Reading not found", exception.getMessage());

        verify(repository).findById(id);
    }
}