package id.ac.ui.cs.advprog.yomubackend.discussion.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomubackend.discussion.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomubackend.discussion.service.CommentService;

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
    public ResponseEntity<?> getCommentsByReadingId(@PathVariable UUID readingId,
            @AuthenticationPrincipal User user) {
        UUID userId = user != null ? user.getId() : null;
        List<CommentResponse> comments = commentService.getCommentsByReadingId(readingId, userId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/readings/{readingId}/comments")
    public ResponseEntity<?> createComment(@PathVariable UUID readingId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user) {
        try {
            UUID userId = requireUserId(user);
            CommentResponse created = commentService.createComment(userId, readingId, request);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/readings/{readingId}/comments/{parentId}/replies")
    public ResponseEntity<?> replyToComment(@PathVariable UUID readingId,
            @PathVariable UUID parentId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user) {
        try {
            UUID userId = requireUserId(user);
            CommentResponse created = commentService.replyToComment(
                    userId, readingId, parentId, request);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/readings/{readingId}/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable UUID readingId,
            @PathVariable UUID commentId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user) {
        try {
            UUID userId = requireUserId(user);
            CommentResponse updated = commentService.updateComment(
                    userId, readingId, commentId, request);
            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/readings/{readingId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID readingId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal User user) {
        try {
            UUID userId = requireUserId(user);
            commentService.softDeleteComment(userId, readingId, commentId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/comments/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminDeleteComment(@PathVariable UUID commentId,
            @AuthenticationPrincipal User user) {
        try {
            UUID adminId = requireUserId(user);
            commentService.adminDeleteComment(adminId, commentId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private UUID requireUserId(User user) {
        if (user == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        return user.getId();
    }
}
