package id.ac.ui.cs.advprog.yomubackend.comment.controller;

import id.ac.ui.cs.advprog.yomubackend.comment.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomubackend.comment.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomubackend.comment.service.CommentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private final UUID readingId = UUID.randomUUID();
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
    void getComments_ReturnsListFromService() {
        CommentResponse resp = new CommentResponse();
        resp.setId(UUID.randomUUID());
        when(commentService.getCommentsByReadingId(readingId)).thenReturn(List.of(resp));

        ResponseEntity<?> response = commentController.getCommentsByReadingId(readingId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(commentService, times(1)).getCommentsByReadingId(readingId);
    }

    @Test
    void getComments_EmptyList_ReturnsOkWithEmpty() {
        when(commentService.getCommentsByReadingId(readingId)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = commentController.getCommentsByReadingId(readingId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        verify(commentService, times(1)).getCommentsByReadingId(readingId);
    }

    @Test
    void createComment_Success_ReturnsCreated() {
        CommentRequest req = new CommentRequest();
        req.setContent("hello");
        CommentResponse resp = new CommentResponse();
        when(commentService.createComment(eq(username), eq(readingId), any(CommentRequest.class)))
                .thenReturn(resp);

        ResponseEntity<?> response = commentController.createComment(readingId, req);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        verify(commentService, times(1)).createComment(eq(username), eq(readingId), any(CommentRequest.class));
    }

    @Test
    void createComment_ServiceThrowsIllegalArg_ReturnsBadRequest() {
        CommentRequest req = new CommentRequest();
        req.setContent(" ");
        when(commentService.createComment(eq(username), eq(readingId), any(CommentRequest.class)))
                .thenThrow(new IllegalArgumentException("Isi komentar tidak boleh kosong!"));

        ResponseEntity<?> response = commentController.createComment(readingId, req);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertEquals("Isi komentar tidak boleh kosong!",
                ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void replyToComment_Success_ReturnsCreated() {
        UUID parentId = UUID.randomUUID();
        CommentRequest req = new CommentRequest();
        req.setContent("reply");
        CommentResponse resp = new CommentResponse();
        when(commentService.replyToComment(eq(username), eq(readingId), eq(parentId), any(CommentRequest.class)))
                .thenReturn(resp);

        ResponseEntity<?> response = commentController.replyToComment(readingId, parentId, req);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        verify(commentService, times(1))
                .replyToComment(eq(username), eq(readingId), eq(parentId), any(CommentRequest.class));
    }

    @Test
    void replyToComment_ParentNotFound_ReturnsBadRequest() {
        UUID parentId = UUID.randomUUID();
        CommentRequest req = new CommentRequest();
        req.setContent("reply");
        when(commentService.replyToComment(eq(username), eq(readingId), eq(parentId), any(CommentRequest.class)))
                .thenThrow(new IllegalArgumentException("Komentar induk tidak ditemukan!"));

        ResponseEntity<?> response = commentController.replyToComment(readingId, parentId, req);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    void updateComment_Success_ReturnsOk() {
        UUID commentId = UUID.randomUUID();
        CommentRequest req = new CommentRequest();
        req.setContent("updated");
        CommentResponse resp = new CommentResponse();
        when(commentService.updateComment(eq(username), eq(readingId), eq(commentId), any(CommentRequest.class)))
                .thenReturn(resp);

        ResponseEntity<?> response = commentController.updateComment(readingId, commentId, req);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }

    @Test
    void updateComment_NotAuthor_ReturnsForbidden() {
        UUID commentId = UUID.randomUUID();
        CommentRequest req = new CommentRequest();
        req.setContent("updated");
        when(commentService.updateComment(eq(username), eq(readingId), eq(commentId), any(CommentRequest.class)))
                .thenThrow(new AccessDeniedException("Anda tidak memiliki akses untuk komentar ini!"));

        ResponseEntity<?> response = commentController.updateComment(readingId, commentId, req);

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCode().value());
    }

    @Test
    void updateComment_NotFound_ReturnsBadRequest() {
        UUID commentId = UUID.randomUUID();
        CommentRequest req = new CommentRequest();
        req.setContent("updated");
        when(commentService.updateComment(eq(username), eq(readingId), eq(commentId), any(CommentRequest.class)))
                .thenThrow(new IllegalArgumentException("Komentar tidak ditemukan!"));

        ResponseEntity<?> response = commentController.updateComment(readingId, commentId, req);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    void deleteComment_Success_ReturnsNoContent() {
        UUID commentId = UUID.randomUUID();
        doNothing().when(commentService).softDeleteComment(username, readingId, commentId);

        ResponseEntity<?> response = commentController.deleteComment(readingId, commentId);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode().value());
        verify(commentService, times(1)).softDeleteComment(username, readingId, commentId);
    }

    @Test
    void deleteComment_NotAuthor_ReturnsForbidden() {
        UUID commentId = UUID.randomUUID();
        doThrow(new AccessDeniedException("Anda tidak memiliki akses untuk komentar ini!"))
                .when(commentService).softDeleteComment(username, readingId, commentId);

        ResponseEntity<?> response = commentController.deleteComment(readingId, commentId);

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCode().value());
    }

    @Test
    void deleteComment_NotFound_ReturnsBadRequest() {
        UUID commentId = UUID.randomUUID();
        doThrow(new IllegalArgumentException("Komentar tidak ditemukan!"))
                .when(commentService).softDeleteComment(username, readingId, commentId);

        ResponseEntity<?> response = commentController.deleteComment(readingId, commentId);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }
}