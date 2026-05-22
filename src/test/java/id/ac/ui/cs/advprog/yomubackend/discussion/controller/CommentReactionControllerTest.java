package id.ac.ui.cs.advprog.yomubackend.discussion.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.discussion.dto.ReactionRequest;
import id.ac.ui.cs.advprog.yomubackend.discussion.entity.ReactionType;
import id.ac.ui.cs.advprog.yomubackend.discussion.service.CommentReactionService;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentReactionControllerTest {

    @Mock
    private CommentReactionService reactionService;

    @InjectMocks
    private CommentReactionController reactionController;

    private final UUID commentId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setUsername("reader01");
    }

    @Test
    void addOrUpdateReaction_Success_ReturnsCreated() {
        ReactionRequest req = new ReactionRequest();
        req.setReactionType(ReactionType.UPVOTE);
        doNothing().when(reactionService).addOrUpdateReaction(eq(userId), eq(commentId), any(ReactionRequest.class));

        ResponseEntity<?> response = reactionController.addOrUpdateReaction(commentId, req, user);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        verify(reactionService, times(1)).addOrUpdateReaction(eq(userId), eq(commentId), any(ReactionRequest.class));
    }

    @Test
    void addOrUpdateReaction_ServiceThrows_ReturnsBadRequest() {
        ReactionRequest req = new ReactionRequest();
        req.setReactionType(ReactionType.UPVOTE);
        doThrow(new IllegalArgumentException("Komentar tidak ditemukan!"))
                .when(reactionService).addOrUpdateReaction(eq(userId), eq(commentId), any(ReactionRequest.class));

        ResponseEntity<?> response = reactionController.addOrUpdateReaction(commentId, req, user);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertEquals("Komentar tidak ditemukan!", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void removeReaction_Success_ReturnsNoContent() {
        doNothing().when(reactionService).removeReaction(userId, commentId);

        ResponseEntity<?> response = reactionController.removeReaction(commentId, user);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
        verify(reactionService, times(1)).removeReaction(userId, commentId);
    }

    @Test
    void removeReaction_ServiceThrows_ReturnsBadRequest() {
        doThrow(new IllegalArgumentException("User tidak ditemukan!"))
                .when(reactionService).removeReaction(userId, commentId);

        ResponseEntity<?> response = reactionController.removeReaction(commentId, user);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    void addOrUpdateReaction_WithNullAuth_ThrowsAccessDeniedException() {
        ReactionRequest req = new ReactionRequest();
        req.setReactionType(ReactionType.UPVOTE);

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.security.access.AccessDeniedException.class,
                () -> reactionController.addOrUpdateReaction(commentId, req, null));
    }
}
