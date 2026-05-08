package id.ac.ui.cs.advprog.yomubackend.comment.service;

import id.ac.ui.cs.advprog.yomubackend.comment.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomubackend.comment.dto.CommentResponse;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    CommentResponse createComment(String username, UUID readingId, CommentRequest request);

    CommentResponse replyToComment(String username, UUID readingId, UUID parentCommentId,
            CommentRequest request);

    List<CommentResponse> getCommentsByReadingId(UUID readingId, String username);

    CommentResponse updateComment(String username, UUID readingId, UUID commentId,
            CommentRequest request);

    void softDeleteComment(String username, UUID readingId, UUID commentId);

    void adminDeleteComment(String username, UUID commentId);
}
