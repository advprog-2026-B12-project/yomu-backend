package id.ac.ui.cs.advprog.yomubackend.quiz.controller;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizSubmitRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.ReadingListItemResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.exception.QuizAlreadyCompletedException;
import id.ac.ui.cs.advprog.yomubackend.quiz.mapper.QuizResponseMapper;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Option;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuizService;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuizSessionService;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.ReadingProgressService;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.ReadingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

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
    private QuizSessionService quizSessionService;
    @Mock
    private ReadingProgressService readingProgressService;

    private QuizController quizController;

    private UUID readingId;
    private UUID userId;
    private Reading reading;
    private User user;

    @BeforeEach
    void setUp() {
        readingId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        Option correctOption = new Option();
        correctOption.setId(UUID.randomUUID());
        correctOption.setOptionText("Correct");
        correctOption.setCorrect(true);

        Option wrongOption = new Option();
        wrongOption.setId(UUID.randomUUID());
        wrongOption.setOptionText("Wrong");
        wrongOption.setCorrect(false);

        Question question = new Question();
        question.setId(UUID.randomUUID());
        question.setQuestionText("Question?");
        question.setOptions(List.of(correctOption, wrongOption));

        reading = new Reading();
        reading.setId(readingId);
        reading.setTitle("Test Reading");
        reading.setCategory("Science");
        reading.setContent("Hidden Content");
        reading.setQuestions(List.of(question));

        quizController = new QuizController(
                readingService,
                quizService,
                new QuizResponseMapper(),
                quizSessionService,
                readingProgressService
        );
    }

    @Test
    void getQuiz_returnsQuizWithoutReadingContentOrCorrectAnswers() {
        when(readingService.findById(readingId)).thenReturn(reading);

        QuizResponse result = quizController.getQuiz(readingId, user);

        assertEquals(readingId, result.getId());
        assertEquals("Test Reading", result.getTitle());
        assertEquals("Science", result.getCategory());
        assertEquals(1, result.getQuestions().size());
        assertEquals(2, result.getQuestions().getFirst().getOptions().size());
        verify(quizService).ensureNotCompleted(userId, readingId);
        verify(readingProgressService).ensureOpened(userId, readingId);
        verify(quizSessionService).start(userId, readingId);
        verify(readingService).findById(readingId);
    }

    @Test
    void submit_returnsQuizResultResponse() {
        QuizSubmitRequest request = new QuizSubmitRequest();
        QuizResultResponse response = new QuizResultResponse(3, 5);
        when(quizService.submit(userId, request)).thenReturn(response);
        ResponseEntity<?> result = quizController.submit(request, user);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());
        verify(quizService).submit(userId, request);
    }

    @Test
    void getAll_returnsAllReadings() {
        List<Reading> readings = List.of(reading);
        when(readingService.findAll()).thenReturn(readings);
        when(quizService.hasCompleted(userId, readingId)).thenReturn(true);

        List<ReadingListItemResponse> result = quizController.getAll(user);

        assertEquals(1, result.size());
        assertTrue(result.getFirst().isCompleted());
        verify(readingService).findAll();
    }

    @Test
    void submit_whenQuizAlreadyCompleted_throwsException() {
        QuizSubmitRequest request = new QuizSubmitRequest();

        when(quizService.submit(userId, request))
                .thenThrow(new QuizAlreadyCompletedException());

        assertThrows(QuizAlreadyCompletedException.class, () -> quizController.submit(request, user));

        verify(quizService).submit(userId, request);
    }

    @Test
    void getQuizStatus_returnsCompletionStatus() {
        when(quizService.hasCompleted(userId, readingId)).thenReturn(true);

        Map<String, Boolean> result =
                quizController.getQuizStatus(readingId, user);

        assertTrue(result.get("completed"));

        verify(quizService).hasCompleted(userId, readingId);
    }
}
