package id.ac.ui.cs.advprog.yomubackend.comment.dto;

import id.ac.ui.cs.advprog.yomubackend.comment.entity.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CommentResponse {

    private UUID id;
    private UUID readingId;
    private UUID authorId;
    private UUID parentId;
    private String content;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime editedAt;
    private List<CommentResponse> replies = new ArrayList<>();

    public static CommentResponse fromEntity(Comment comment, List<CommentResponse> replies) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setReadingId(comment.getReadingId());
        response.setAuthorId(comment.getAuthorId());
        response.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        response.setContent(comment.getContent());
        response.setDeleted(comment.isDeleted());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        response.setEditedAt(comment.getEditedAt());
        response.setReplies(replies != null ? new ArrayList<>(replies) : new ArrayList<>());
        return response;
    }
}
