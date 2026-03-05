package id.ac.ui.cs.advprog.yomubackend.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void givenBlankComment_whenFieldsAreSet_thenFieldsAreCorrect() {
        UUID id = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Comment comment = new Comment();
        comment.setId(id);
        comment.setReadingId(readingId);
        comment.setAuthorId(authorId);
        comment.setContent("This is a comment");
        comment.setDeleted(false);
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);

        assertEquals(id, comment.getId());
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
        Comment parent = new Comment();
        parent.setId(UUID.randomUUID());
        parent.setContent("Parent comment");

        Comment reply = new Comment();
        reply.setId(UUID.randomUUID());
        reply.setContent("Reply comment");
        reply.setParent(parent);

        assertEquals(parent, reply.getParent());
    }

    @Test
    void givenDeletedComment_whenDeletedFieldsAreSet_thenDeletedFieldsAreCorrect() {
        UUID deletedBy = UUID.randomUUID();
        LocalDateTime deletedAt = LocalDateTime.now();

        Comment comment = new Comment();
        comment.setDeleted(true);
        comment.setDeletedBy(deletedBy);
        comment.setDeletedAt(deletedAt);

        assertTrue(comment.isDeleted());
        assertEquals(deletedBy, comment.getDeletedBy());
        assertEquals(deletedAt, comment.getDeletedAt());
    }

    @Test
    void givenTwoCommentsWithSameId_whenEqualsIsChecked_thenTheyAreEqual() {
        UUID id = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Comment c1 = new Comment();
        c1.setId(id);
        c1.setReadingId(readingId);
        c1.setAuthorId(authorId);
        c1.setContent("Same");
        c1.setCreatedAt(now);
        c1.setUpdatedAt(now);

        Comment c2 = new Comment();
        c2.setId(id);
        c2.setReadingId(readingId);
        c2.setAuthorId(authorId);
        c2.setContent("Same");
        c2.setCreatedAt(now);
        c2.setUpdatedAt(now);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertTrue(c1.toString().contains("Same"));
    }
}

