package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizSubmitRequest;
import id.ac.ui.cs.advprog.yomubackend.shared.events.quiz.QuizFinishedEvent;
import org.springframework.context.ApplicationEventPublisher;
import id.ac.ui.cs.advprog.yomubackend.quiz.exception.ReadingNotOpenedException;
import id.ac.ui.cs.advprog.yomubackend.quiz.exception.QuizAlreadyCompletedException;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Option;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceImplTest {

    @Mock
    private ReadingService readingService;
    @Mock
    private QuizAttemptRepository quizAttemptRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private ReadingProgressService readingProgressService;

    @InjectMocks
    private QuizServiceImpl quizService;

    private UUID readingId;
    private UUID userId;
    private UUID correctOptionId;
    private Reading reading;
    private Question question;
    private Option correctOption;
    private Option wrongOption;

    @BeforeEach
    void setUp() {
        readingId = UUID.randomUUID();
        userId = UUID.randomUUID();
        correctOptionId = UUID.randomUUID();

        correctOption = new Option();
        correctOption.setId(correctOptionId);
        correctOption.setCorrect(true);
        correctOption.setOptionText("Correct");

        wrongOption = new Option();
        wrongOption.setId(UUID.randomUUID());
        wrongOption.setCorrect(false);
        wrongOption.setOptionText("Wrong");

        question = new Question();
        question.setId(UUID.randomUUID());
        question.setQuestionText("A question?");
        question.setOptions(List.of(correctOption, wrongOption));

        reading = new Reading();
        reading.setId(readingId);
        reading.setTitle("Test");
        reading.setQuestions(List.of(question));
    }

    @Test
    void submit_withCorrectAnswer_returnsScoreOne() {
        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setUserId(userId);
        request.setReadingId(readingId);
        request.setAnswers(Map.of(question.getId().toString(), correctOptionId.toString()));

        when(readingService.findById(readingId)).thenReturn(reading);

        QuizResultResponse result = quizService.submit(userId, request);

        assertEquals(1, result.getScore());
        assertEquals(1, result.getTotal());

        ArgumentCaptor<QuizAttempt> captor = ArgumentCaptor.forClass(QuizAttempt.class);
        verify(quizAttemptRepository).save(captor.capture());
        verify(readingProgressService).ensureOpened(userId, readingId);
        QuizAttempt saved = captor.getValue();
        assertEquals(userId, saved.getUserId());
        assertEquals(readingId, saved.getReadingId());
        assertEquals(1, saved.getScore());
        assertEquals(1, saved.getTotal());
        assertNotNull(saved.getCreatedAt());

        ArgumentCaptor<QuizFinishedEvent> eventCaptor = ArgumentCaptor.forClass(QuizFinishedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        QuizFinishedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(userId, publishedEvent.userId());
        assertEquals(readingId, publishedEvent.readingId());
        assertEquals(1, publishedEvent.score());
        assertEquals(1, publishedEvent.total());
        assertTrue(publishedEvent.isPerfectScore());
    }

    @Test
    void submit_withWrongAnswer_returnsScoreZero() {
        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setUserId(userId);
        request.setReadingId(readingId);
        request.setAnswers(Map.of(question.getId().toString(), wrongOption.getId().toString()));

        when(readingService.findById(readingId)).thenReturn(reading);

        QuizResultResponse result = quizService.submit(userId, request);

        assertEquals(0, result.getScore());
        assertEquals(1, result.getTotal());
    }

    @Test
    void submit_withNoAnswerProvided_returnsScoreZero() {
        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setUserId(userId);
        request.setReadingId(readingId);
        request.setAnswers(Map.of()); // no answer for the question

        when(readingService.findById(readingId)).thenReturn(reading);

        QuizResultResponse result = quizService.submit(userId, request);

        assertEquals(0, result.getScore());
        assertEquals(1, result.getTotal());
    }

    @Test
    void submit_withNoCorrectOption_doesNotCount() {
        // Question where no option is marked correct
        Option opt = new Option();
        opt.setId(UUID.randomUUID());
        opt.setCorrect(false);

        Question q = new Question();
        q.setId(UUID.randomUUID());
        q.setOptions(List.of(opt));

        Reading r = new Reading();
        r.setId(readingId);
        r.setQuestions(List.of(q));

        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setUserId(userId);
        request.setReadingId(readingId);
        request.setAnswers(Map.of(q.getId().toString(), opt.getId().toString()));

        when(readingService.findById(readingId)).thenReturn(r);

        QuizResultResponse result = quizService.submit(userId, request);

        assertEquals(0, result.getScore());
        assertEquals(1, result.getTotal());
    }

    @Test
    void submit_withCompletedQuiz_throwsAlreadyCompletedException() {
        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setUserId(userId);
        request.setReadingId(readingId);
        request.setAnswers(Map.of());

        when(quizAttemptRepository.existsByUserIdAndReadingId(userId, readingId)).thenReturn(true);

        assertThrows(QuizAlreadyCompletedException.class, () -> quizService.submit(userId, request));
        verify(quizAttemptRepository, never()).save(any());
        verify(readingProgressService, never()).ensureOpened(any(), any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void submit_withoutOpeningReading_throwsReadingNotOpenedException() {
        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setUserId(userId);
        request.setReadingId(readingId);
        request.setAnswers(Map.of());

        doThrow(new ReadingNotOpenedException())
                .when(readingProgressService).ensureOpened(userId, readingId);

        assertThrows(ReadingNotOpenedException.class, () -> quizService.submit(userId, request));
        verify(readingService, never()).findById(any());
        verify(quizAttemptRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void hasCompleted_delegatesToRepository() {
        when(quizAttemptRepository.existsByUserIdAndReadingId(userId, readingId)).thenReturn(true);

        assertTrue(quizService.hasCompleted(userId, readingId));

        verify(quizAttemptRepository).existsByUserIdAndReadingId(userId, readingId);
    }

    @Test
    void submit_withoutUserId_throwsException() {
        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setReadingId(readingId);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> quizService.submit(null, request));

        assertEquals("User ID is required", exception.getMessage());
    }

    @Test
    void submit_withoutReadingId_throwsException() {
        QuizSubmitRequest request = new QuizSubmitRequest();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> quizService.submit(userId, request));

        assertEquals("Reading ID is required", exception.getMessage());
    }
}
