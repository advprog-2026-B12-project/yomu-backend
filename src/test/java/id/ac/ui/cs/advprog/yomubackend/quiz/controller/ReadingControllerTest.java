package id.ac.ui.cs.advprog.yomubackend.quiz.controller;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.ReadingListItemResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.ReadingResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.exception.QuizAlreadyStartedException;
import id.ac.ui.cs.advprog.yomubackend.quiz.mapper.QuizResponseMapper;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuizService;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuizSessionService;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.ReadingProgressService;
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
    @Mock
    private QuizService quizService;
    @Mock
    private QuizSessionService quizSessionService;
    @Mock
    private ReadingProgressService readingProgressService;

    private ReadingController readingController;

    private UUID id;
    private UUID userId;
    private Reading reading;
    private User user;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        reading = new Reading();
        reading.setId(id);
        reading.setTitle("Reading Title");
        reading.setCategory("News & Media");
        reading.setContent("Reading Content");

        readingController = new ReadingController(
                readingService,
                quizService,
                new QuizResponseMapper(),
                quizSessionService,
                readingProgressService
        );
    }

    @Test
    void getById_returnsReading() {
        when(readingService.findById(id)).thenReturn(reading);
        ReadingResponse result = readingController.getById(id, user);
        assertEquals(id, result.getId());
        assertEquals("Reading Title", result.getTitle());
        assertEquals("News & Media", result.getCategory());
        assertEquals("Reading Content", result.getContent());
        verify(quizService).ensureNotCompleted(userId, id);
        verify(quizSessionService).ensureNotStarted(userId, id);
        verify(readingService).findById(id);
        verify(readingProgressService).markOpened(userId, id);
    }

    @Test
    void getById_whenQuizAlreadyStarted_doesNotReturnReading() {
        doThrow(new QuizAlreadyStartedException())
                .when(quizSessionService).ensureNotStarted(userId, id);

        assertThrows(
                QuizAlreadyStartedException.class,
                () -> readingController.getById(id, user)
        );

        verify(quizService).ensureNotCompleted(userId, id);
        verify(quizSessionService).ensureNotStarted(userId, id);
        verify(readingService, never()).findById(id);
        verify(readingProgressService, never()).markOpened(any(), any());
    }

    @Test
    void getAll_returnsAllReadings() {
        List<Reading> readings = List.of(reading);
        when(readingService.findAll()).thenReturn(readings);
        when(quizService.hasCompleted(userId, id)).thenReturn(true);

        List<ReadingListItemResponse> result = readingController.getAll(user);

        assertEquals(1, result.size());
        assertEquals(id, result.getFirst().getId());
        assertEquals("Reading Title", result.getFirst().getTitle());
        assertEquals("News & Media", result.getFirst().getCategory());
        assertTrue(result.getFirst().isCompleted());
        verify(readingService).findAll();
    }
}
