package id.ac.ui.cs.advprog.yomubackend.discussion.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.discussion.dto.ReactionRequest;
import id.ac.ui.cs.advprog.yomubackend.discussion.service.CommentReactionService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class CommentReactionController {

    private final CommentReactionService reactionService;

    public CommentReactionController(CommentReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @PostMapping("/comments/{commentId}/reactions")
    public ResponseEntity<?> addOrUpdateReaction(@PathVariable UUID commentId,
            @Valid @RequestBody ReactionRequest request,
            @AuthenticationPrincipal User user) {
        try {
            UUID userId = requireUserId(user);
            reactionService.addOrUpdateReaction(userId, commentId, request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/comments/{commentId}/reactions")
    public ResponseEntity<?> removeReaction(@PathVariable UUID commentId,
            @AuthenticationPrincipal User user) {
        try {
            UUID userId = requireUserId(user);
            reactionService.removeReaction(userId, commentId);
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
