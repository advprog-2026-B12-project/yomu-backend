package id.ac.ui.cs.advprog.yomubackend.discussion.listener;

import id.ac.ui.cs.advprog.yomubackend.discussion.repository.CommentRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.quiz.ReadingDeletedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component("discussionReadingDeletedEventListener")
public class ReadingDeletedEventListener {

    private final CommentRepository commentRepository;

    public ReadingDeletedEventListener(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReadingDeleted(ReadingDeletedEvent event) {
        commentRepository.deleteAllByReadingId(event.readingId());
    }
}
