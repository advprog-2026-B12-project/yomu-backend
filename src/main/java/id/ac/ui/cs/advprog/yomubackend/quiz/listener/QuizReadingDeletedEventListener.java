package id.ac.ui.cs.advprog.yomubackend.quiz.listener;

import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizSessionRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingProgressRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.quiz.ReadingDeletedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class QuizReadingDeletedEventListener {

    private final ReadingProgressRepository readingProgressRepository;
    private final QuizSessionRepository quizSessionRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    public QuizReadingDeletedEventListener(ReadingProgressRepository readingProgressRepository,
                                           QuizSessionRepository quizSessionRepository,
                                           QuizAttemptRepository quizAttemptRepository) {
        this.readingProgressRepository = readingProgressRepository;
        this.quizSessionRepository = quizSessionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
    }

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReadingDeleted(ReadingDeletedEvent event) {
        readingProgressRepository.deleteByReadingId(event.readingId());
        quizSessionRepository.deleteByReadingId(event.readingId());
        quizAttemptRepository.deleteByReadingId(event.readingId());
    }
}
