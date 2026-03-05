package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
}