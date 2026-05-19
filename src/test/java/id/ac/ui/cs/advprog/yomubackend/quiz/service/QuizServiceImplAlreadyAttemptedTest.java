package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.completion.QuizCompletionProcessor;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizSubmitRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.exception.QuizAlreadyCompletedException;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceImplAlreadyAttemptedTest {

    @Mock
    private ReadingService readingService;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private QuizCompletionProcessor quizCompletionProcessor;

    @Mock
    private ReadingProgressService readingProgressService;

    @InjectMocks
    private QuizServiceImpl quizService;

    @Test
    void submit_WhenAlreadyAttempted_ThrowsQuizAlreadyCompletedException() {
        UUID userId = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();

        QuizSubmitRequest request = new QuizSubmitRequest();
        request.setReadingId(readingId);
        request.setAnswers(Map.of());

        when(quizAttemptRepository.existsByUserIdAndReadingId(userId, readingId)).thenReturn(true);

        assertThrows(QuizAlreadyCompletedException.class, () -> quizService.submit(userId, request));
    }
}
