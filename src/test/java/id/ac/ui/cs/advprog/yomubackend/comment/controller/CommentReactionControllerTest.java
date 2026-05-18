package id.ac.ui.cs.advprog.yomubackend.comment.controller;

import id.ac.ui.cs.advprog.yomubackend.comment.dto.ReactionRequest;
import id.ac.ui.cs.advprog.yomubackend.comment.entity.ReactionType;
import id.ac.ui.cs.advprog.yomubackend.comment.service.CommentReactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

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
    private final String username = "reader01";

    @BeforeEach
    void setUpAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, "N/A"));
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addOrUpdateReaction_Success_ReturnsCreated() {
        ReactionRequest req = new ReactionRequest();
        req.setReactionType(ReactionType.UPVOTE);
        doNothing().when(reactionService).addOrUpdateReaction(eq(username), eq(commentId), any(ReactionRequest.class));

        ResponseEntity<?> response = reactionController.addOrUpdateReaction(commentId, req);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        verify(reactionService, times(1)).addOrUpdateReaction(eq(username), eq(commentId), any(ReactionRequest.class));
    }

    @Test
    void addOrUpdateReaction_ServiceThrows_ReturnsBadRequest() {
        ReactionRequest req = new ReactionRequest();
        req.setReactionType(ReactionType.UPVOTE);
        doThrow(new IllegalArgumentException("Komentar tidak ditemukan!"))
                .when(reactionService).addOrUpdateReaction(eq(username), eq(commentId), any(ReactionRequest.class));

        ResponseEntity<?> response = reactionController.addOrUpdateReaction(commentId, req);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertEquals("Komentar tidak ditemukan!", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void removeReaction_Success_ReturnsNoContent() {
        doNothing().when(reactionService).removeReaction(username, commentId);

        ResponseEntity<?> response = reactionController.removeReaction(commentId);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
        verify(reactionService, times(1)).removeReaction(username, commentId);
    }

    @Test
    void removeReaction_ServiceThrows_ReturnsBadRequest() {
        doThrow(new IllegalArgumentException("User tidak ditemukan!"))
                .when(reactionService).removeReaction(username, commentId);

        ResponseEntity<?> response = reactionController.removeReaction(commentId);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }
}
