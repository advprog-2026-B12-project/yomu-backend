package id.ac.ui.cs.advprog.yomubackend.comment.controller;

import id.ac.ui.cs.advprog.yomubackend.comment.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomubackend.comment.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomubackend.comment.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/readings/{readingId}/comments")
    public ResponseEntity<?> getCommentsByReadingId(@PathVariable UUID readingId) {
        List<CommentResponse> comments = commentService.getCommentsByReadingId(readingId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/readings/{readingId}/comments")
    public ResponseEntity<?> createComment(@PathVariable UUID readingId,
                                           @RequestBody CommentRequest request) {
        try {
            CommentResponse created = commentService.createComment(currentUsername(), readingId, request);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/readings/{readingId}/comments/{parentId}/replies")
    public ResponseEntity<?> replyToComment(@PathVariable UUID readingId,
                                            @PathVariable UUID parentId,
                                            @RequestBody CommentRequest request) {
        try {
            CommentResponse created = commentService.replyToComment(
                    currentUsername(), readingId, parentId, request);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/readings/{readingId}/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable UUID readingId,
                                           @PathVariable UUID commentId,
                                           @RequestBody CommentRequest request) {
        try {
            CommentResponse updated = commentService.updateComment(
                    currentUsername(), readingId, commentId, request);
            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/readings/{readingId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID readingId,
                                           @PathVariable UUID commentId) {
        try {
            commentService.softDeleteComment(currentUsername(), readingId, commentId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
