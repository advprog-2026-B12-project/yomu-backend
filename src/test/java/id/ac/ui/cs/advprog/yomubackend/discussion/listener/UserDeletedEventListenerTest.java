package id.ac.ui.cs.advprog.yomubackend.discussion.listener;

import id.ac.ui.cs.advprog.yomubackend.discussion.repository.CommentRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.auth.UserDeletedEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDeletedEventListenerTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private UserDeletedEventListener listener;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void handleUserDeleted_Success_UpdatesComments() {
        UserDeletedEvent event = new UserDeletedEvent(userId);
        when(commentRepository.softDeleteAllByAuthorId(eq(userId), any(LocalDateTime.class))).thenReturn(3);

        listener.handleUserDeleted(event);

        verify(commentRepository, times(1)).softDeleteAllByAuthorId(eq(userId), any(LocalDateTime.class));
    }

    @Test
    void handleUserDeleted_NoComments_NothingUpdated() {
        UserDeletedEvent event = new UserDeletedEvent(userId);
        when(commentRepository.softDeleteAllByAuthorId(eq(userId), any(LocalDateTime.class))).thenReturn(0);

        listener.handleUserDeleted(event);

        verify(commentRepository, times(1)).softDeleteAllByAuthorId(eq(userId), any(LocalDateTime.class));
    }
}
