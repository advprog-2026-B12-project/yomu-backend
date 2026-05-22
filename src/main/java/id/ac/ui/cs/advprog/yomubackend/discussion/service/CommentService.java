package id.ac.ui.cs.advprog.yomubackend.discussion.service;

import java.util.List;
import java.util.UUID;

import id.ac.ui.cs.advprog.yomubackend.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomubackend.discussion.dto.CommentResponse;

public interface CommentService {

        CommentResponse createComment(UUID userId, UUID readingId, CommentRequest request);

        CommentResponse replyToComment(UUID userId, UUID readingId, UUID parentCommentId,
                        CommentRequest request);

        List<CommentResponse> getCommentsByReadingId(UUID readingId, UUID userId);

        CommentResponse updateComment(UUID userId, UUID readingId, UUID commentId,
                        CommentRequest request);

        void softDeleteComment(UUID userId, UUID readingId, UUID commentId);

        void adminDeleteComment(UUID userId, UUID commentId);
}
