package id.ac.ui.cs.advprog.yomubackend.quiz.controller;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizSubmitRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuizService;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.ReadingService;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
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
    @Mock
    private QuizAttemptRepository quizAttemptRepository;

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
        ResponseEntity<?> result = quizController.submit(request);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());
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

    @Test
    void submit_whenQuizAlreadyCompleted_returnsConflict() {
        QuizSubmitRequest request = new QuizSubmitRequest();

        when(quizService.submit(request))
                .thenThrow(new IllegalStateException("Quiz already completed"));

        ResponseEntity<?> result = quizController.submit(request);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());

        Map<String, String> body = (Map<String, String>) result.getBody();

        assertEquals("Quiz already completed", body.get("error"));

        verify(quizService).submit(request);
    }

    @Test
    void getQuizStatus_returnsCompletionStatus() {
        when(quizAttemptRepository
                .existsByUserIdAndReadingId(readingId, readingId))
                .thenReturn(true);

        Map<String, Boolean> result =
                quizController.getQuizStatus(readingId, readingId);

        assertTrue(result.get("completed"));

        verify(quizAttemptRepository)
                .existsByUserIdAndReadingId(readingId, readingId);
    }
}