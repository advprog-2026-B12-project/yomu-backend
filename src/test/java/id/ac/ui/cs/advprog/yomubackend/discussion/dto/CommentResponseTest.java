package id.ac.ui.cs.advprog.yomubackend.discussion.dto;

import org.junit.jupiter.api.Test;
import id.ac.ui.cs.advprog.yomubackend.discussion.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommentResponseTest {

    @Test
    void givenComment_whenFromEntity_thenAllFieldsMapped() {
        UUID id = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = createdAt.plusSeconds(1);
        LocalDateTime editedAt = createdAt.plusMinutes(5);

        Comment comment = new Comment();
        comment.setId(id);
        comment.setReadingId(readingId);
        comment.setAuthorId(authorId);
        comment.setContent("hello world");
        comment.setCreatedAt(createdAt);
        comment.setUpdatedAt(updatedAt);
        comment.setEditedAt(editedAt);

        CommentResponse response = CommentResponse.fromEntity(comment, List.of());

        assertEquals(id, response.getId());
        assertEquals(readingId, response.getReadingId());
        assertEquals(authorId, response.getAuthorId());
        assertEquals("hello world", response.getContent());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
        assertEquals(editedAt, response.getEditedAt());
        assertNull(response.getParentId());
        assertFalse(response.isDeleted());
        assertNotNull(response.getReplies());
        assertTrue(response.getReplies().isEmpty());
    }

    @Test
    void givenCommentWithParent_whenFromEntity_thenParentIdSet() {
        Comment parent = new Comment();
        parent.setId(UUID.randomUUID());

        Comment reply = new Comment();
        reply.setId(UUID.randomUUID());
        reply.setReadingId(UUID.randomUUID());
        reply.setAuthorId(UUID.randomUUID());
        reply.setContent("reply");
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUpdatedAt(LocalDateTime.now());
        reply.setParent(parent);

        CommentResponse response = CommentResponse.fromEntity(reply, List.of());

        assertEquals(parent.getId(), response.getParentId());
    }

    @Test
    void givenSoftDeletedComment_whenFromEntity_thenDeletedFlagReflected() {
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setReadingId(UUID.randomUUID());
        comment.setAuthorId(UUID.randomUUID());
        comment.setContent("gone");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setDeleted(true);

        CommentResponse response = CommentResponse.fromEntity(comment, List.of());

        assertTrue(response.isDeleted());
    }

    @Test
    void givenCommentWithReplies_whenFromEntity_thenRepliesAttached() {
        Comment parent = new Comment();
        parent.setId(UUID.randomUUID());
        parent.setReadingId(UUID.randomUUID());
        parent.setAuthorId(UUID.randomUUID());
        parent.setContent("parent");
        parent.setCreatedAt(LocalDateTime.now());
        parent.setUpdatedAt(LocalDateTime.now());

        CommentResponse child = new CommentResponse();
        child.setId(UUID.randomUUID());

        CommentResponse response = CommentResponse.fromEntity(parent, List.of(child));

        assertEquals(1, response.getReplies().size());
        assertEquals(child.getId(), response.getReplies().get(0).getId());
    }

    @Test
    void givenNullReplies_whenFromEntity_thenRepliesIsEmptyList() {
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setReadingId(UUID.randomUUID());
        comment.setAuthorId(UUID.randomUUID());
        comment.setContent("test");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        CommentResponse response = CommentResponse.fromEntity(comment, null);

        assertNotNull(response.getReplies());
        assertTrue(response.getReplies().isEmpty());
    }
}
