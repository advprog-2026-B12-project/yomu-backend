package id.ac.ui.cs.advprog.yomubackend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    private UUID commentId;
    private UUID readingId;
    private UUID authorId;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        commentId = UUID.randomUUID();
        readingId = UUID.randomUUID();
        authorId = UUID.randomUUID();
        now = LocalDateTime.now();
    }

    private Comment buildComment(UUID id, String content) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setReadingId(readingId);
        comment.setAuthorId(authorId);
        comment.setContent(content);
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);
        return comment;
    }

    @Test
    void givenBlankComment_whenFieldsAreSet_thenFieldsAreCorrect() {
        Comment comment = buildComment(commentId, "This is a comment");
        comment.setDeleted(false);

        assertEquals(commentId, comment.getId());
        assertEquals(readingId, comment.getReadingId());
        assertEquals(authorId, comment.getAuthorId());
        assertEquals("This is a comment", comment.getContent());
        assertFalse(comment.isDeleted());
        assertEquals(now, comment.getCreatedAt());
        assertEquals(now, comment.getUpdatedAt());
        assertNull(comment.getParent());
        assertNull(comment.getDeletedBy());
        assertNull(comment.getDeletedAt());
    }

    @Test
    void givenCommentWithParent_whenParentIsSet_thenParentIsCorrect() {
        Comment parent = buildComment(UUID.randomUUID(), "Parent comment");
        Comment reply = buildComment(UUID.randomUUID(), "Reply comment");
        reply.setParent(parent);

        assertEquals(parent, reply.getParent());
    }

    @Test
    void givenDeletedComment_whenDeletedFieldsAreSet_thenDeletedFieldsAreCorrect() {
        UUID deletedBy = UUID.randomUUID();
        LocalDateTime deletedAt = LocalDateTime.now();

        Comment comment = buildComment(commentId, "To be deleted");
        comment.setDeleted(true);
        comment.setDeletedBy(deletedBy);
        comment.setDeletedAt(deletedAt);

        assertTrue(comment.isDeleted());
        assertEquals(deletedBy, comment.getDeletedBy());
        assertEquals(deletedAt, comment.getDeletedAt());
    }

    @Test
    void givenTwoCommentsWithSameId_whenEqualsIsChecked_thenTheyAreEqual() {
        Comment c1 = buildComment(commentId, "Same");
        Comment c2 = buildComment(commentId, "Same");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertTrue(c1.toString().contains("Same"));
    }
}
