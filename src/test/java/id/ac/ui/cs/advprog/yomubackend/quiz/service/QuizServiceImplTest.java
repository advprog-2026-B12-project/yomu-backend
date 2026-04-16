package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizSubmitRequest;
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

        QuizResultResponse result = quizService.submit(request);

        assertEquals(1, result.getScore());
        assertEquals(1, result.getTotal());

        ArgumentCaptor<QuizAttempt> captor = ArgumentCaptor.forClass(QuizAttempt.class);
        verify(quizAttemptRepository).save(captor.capture());
        QuizAttempt saved = captor.getValue();
        assertEquals(userId, saved.getUserId());
        assertEquals(readingId, saved.getReadingId());
        assertEquals(1, saved.getScore());
        assertEquals(1, saved.getTotal());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void submit_withWrongAnswer_returnsScoreZero() {
        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setUserId(userId);
        request.setReadingId(readingId);
        request.setAnswers(Map.of(question.getId().toString(), wrongOption.getId().toString()));

        when(readingService.findById(readingId)).thenReturn(reading);

        QuizResultResponse result = quizService.submit(request);

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

        QuizResultResponse result = quizService.submit(request);

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

        QuizResultResponse result = quizService.submit(request);

        assertEquals(0, result.getScore());
        assertEquals(1, result.getTotal());
    }
}