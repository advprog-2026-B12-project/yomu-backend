package id.ac.ui.cs.advprog.yomubackend.discussion.service;

import java.util.List;
import java.util.UUID;

import id.ac.ui.cs.advprog.yomubackend.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomubackend.discussion.dto.CommentResponse;

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
