package id.ac.ui.cs.advprog.yomubackend.discussion.listener;

import id.ac.ui.cs.advprog.yomubackend.discussion.repository.CommentRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.auth.UserDeletedEvent;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;

@Component
public class UserDeletedEventListener {

    private final CommentRepository commentRepository;

    public UserDeletedEventListener(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleUserDeleted(UserDeletedEvent event) {
        LocalDateTime now = LocalDateTime.now();
        int affected = commentRepository.softDeleteAllByAuthorId(event.userId(), now);
        System.out.println("Soft deleted " + affected + " comment(s) for user " + event.userId());
    }
}
