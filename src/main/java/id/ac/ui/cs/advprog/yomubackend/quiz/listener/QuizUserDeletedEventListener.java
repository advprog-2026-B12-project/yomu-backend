package id.ac.ui.cs.advprog.yomubackend.quiz.listener;

import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizSessionRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingProgressRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.auth.UserDeletedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class QuizUserDeletedEventListener {

    private final QuizAttemptRepository quizAttemptRepository;
    private final ReadingProgressRepository readingProgressRepository;
    private final QuizSessionRepository quizSessionRepository;

    public QuizUserDeletedEventListener(QuizAttemptRepository quizAttemptRepository,
                                        ReadingProgressRepository readingProgressRepository,
                                        QuizSessionRepository quizSessionRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.readingProgressRepository = readingProgressRepository;
        this.quizSessionRepository = quizSessionRepository;
    }

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleUserDeleted(UserDeletedEvent event) {
        quizAttemptRepository.deleteByUserId(event.userId());
        readingProgressRepository.deleteByUserId(event.userId());
        quizSessionRepository.deleteByUserId(event.userId());
    }
}
