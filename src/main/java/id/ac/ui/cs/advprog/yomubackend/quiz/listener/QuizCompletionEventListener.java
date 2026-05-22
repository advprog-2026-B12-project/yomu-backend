package id.ac.ui.cs.advprog.yomubackend.quiz.listener;

import id.ac.ui.cs.advprog.yomubackend.quiz.completion.QuizCompletion;
import id.ac.ui.cs.advprog.yomubackend.quiz.completion.QuizCompletionProcessor;
import id.ac.ui.cs.advprog.yomubackend.shared.events.quiz.QuizFinishedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
public class QuizCompletionEventListener {

    private final List<QuizCompletionProcessor> processors;

    public QuizCompletionEventListener(List<QuizCompletionProcessor> processors) {
        this.processors = processors;
    }

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleQuizFinished(QuizFinishedEvent event) {
        QuizCompletion completion = new QuizCompletion(
                event.userId(),
                event.readingId(),
                event.score(),
                event.total(),
                event.timestamp()
        );
        processors.forEach(processor -> processor.processCompletion(completion));
    }
}
