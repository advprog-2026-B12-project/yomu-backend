package id.ac.ui.cs.advprog.yomubackend.discussion.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
                                                    @Valid @RequestBody ReactionRequest request) {
        try {
            reactionService.addOrUpdateReaction(currentUsername(), commentId, request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/comments/{commentId}/reactions")
    public ResponseEntity<?> removeReaction(@PathVariable UUID commentId) {
        try {
            reactionService.removeReaction(currentUsername(), commentId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
