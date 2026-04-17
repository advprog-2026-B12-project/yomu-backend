package id.ac.ui.cs.advprog.yomubackend.quiz.controller;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizSubmitRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuizService;
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
class QuizControllerTest {

    @Mock
    private ReadingService readingService;
    @Mock
    private QuizService quizService;
    @Mock
    private ReadingRepository readingRepository;

    @InjectMocks
    private QuizController quizController;

    private UUID readingId;
    private Reading reading;

    @BeforeEach
    void setUp() {
        readingId = UUID.randomUUID();
        reading = new Reading();
        reading.setId(readingId);
        reading.setTitle("Test Reading");
    }

    @Test
    void getQuiz_returnsReading() {
        when(readingService.findById(readingId)).thenReturn(reading);
        Reading result = quizController.getQuiz(readingId);
        assertEquals(reading, result);
        verify(readingService).findById(readingId);
    }

    @Test
    void submit_returnsQuizResultResponse() {
        QuizSubmitRequest request = new QuizSubmitRequest();
        QuizResultResponse response = new QuizResultResponse(3, 5);
        when(quizService.submit(request)).thenReturn(response);
        QuizResultResponse result = quizController.submit(request);
        assertEquals(response, result);
        verify(quizService).submit(request);
    }

    @Test
    void getAll_returnsAllReadings() {
        List<Reading> readings = List.of(reading);
        when(readingRepository.findAll()).thenReturn(readings);
        List<Reading> result = quizController.getAll();
        assertEquals(readings, result);
        verify(readingRepository).findAll();
    }
}