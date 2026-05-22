package id.ac.ui.cs.advprog.yomubackend.quiz.listener;

import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizSessionRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingProgressRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.auth.UserDeletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizUserDeletedEventListenerTest {

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private ReadingProgressRepository readingProgressRepository;

    @Mock
    private QuizSessionRepository quizSessionRepository;

    @InjectMocks
    private QuizUserDeletedEventListener listener;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void handleUserDeleted_DeletesAllQuizDataForUser() {
        UserDeletedEvent event = new UserDeletedEvent(userId);

        listener.handleUserDeleted(event);

        verify(quizAttemptRepository, times(1)).deleteByUserId(userId);
        verify(readingProgressRepository, times(1)).deleteByUserId(userId);
        verify(quizSessionRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    void handleUserDeleted_UserHasNoData_StillCallsAllRepositories() {
        UserDeletedEvent event = new UserDeletedEvent(userId);
        doNothing().when(quizAttemptRepository).deleteByUserId(userId);
        doNothing().when(readingProgressRepository).deleteByUserId(userId);
        doNothing().when(quizSessionRepository).deleteByUserId(userId);

        listener.handleUserDeleted(event);

        verify(quizAttemptRepository, times(1)).deleteByUserId(userId);
        verify(readingProgressRepository, times(1)).deleteByUserId(userId);
        verify(quizSessionRepository, times(1)).deleteByUserId(userId);
    }
}
