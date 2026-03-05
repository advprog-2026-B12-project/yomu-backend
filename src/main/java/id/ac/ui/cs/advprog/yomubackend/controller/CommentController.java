package id.ac.ui.cs.advprog.yomubackend.controller;

import id.ac.ui.cs.advprog.yomubackend.entity.Comment;
import id.ac.ui.cs.advprog.yomubackend.repository.CommentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

    private final CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @GetMapping("/readings/{readingId}/comments")
    public ResponseEntity<List<?>> getCommentsByReadingId(@PathVariable UUID readingId) {
        List<Comment> comments = commentRepository.findByReadingIdAndParentIsNull(readingId);
        return ResponseEntity.ok(comments);
    }
}

