package id.ac.ui.cs.advprog.yomubackend.quiz.controller;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.ReadingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadingControllerTest {

    @Mock
    private ReadingService readingService;

    @InjectMocks
    private ReadingController readingController;

    private UUID id;
    private Reading reading;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        reading = new Reading();
        reading.setId(id);
    }

    @Test
    void getById_returnsReading() {
        when(readingService.findById(id)).thenReturn(reading);
        Reading result = readingController.getById(id);
        assertEquals(reading, result);
        verify(readingService).findById(id);
    }

    @Test
    void getAll_returnsAllReadings() {
        List<Reading> readings = List.of(reading);
        when(readingService.findAll()).thenReturn(readings);
        List<Reading> result = readingController.getAll();
        assertEquals(readings, result);
        verify(readingService).findAll();
    }
}