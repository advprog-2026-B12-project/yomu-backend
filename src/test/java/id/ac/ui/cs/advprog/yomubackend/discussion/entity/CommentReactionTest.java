package id.ac.ui.cs.advprog.yomubackend.discussion.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.yomubackend.discussion.entity.Comment;
import id.ac.ui.cs.advprog.yomubackend.discussion.entity.CommentReaction;
import id.ac.ui.cs.advprog.yomubackend.discussion.entity.ReactionType;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommentReactionTest {

    private UUID reactionId;
    private UUID userId;
    private LocalDateTime now;
    private Comment comment;

    @BeforeEach
    void setUp() {
        reactionId = UUID.randomUUID();
        userId = UUID.randomUUID();
        now = LocalDateTime.now();
        comment = new Comment();
        comment.setId(UUID.randomUUID());
    }

    private CommentReaction buildReaction(UUID id, ReactionType type) {
        CommentReaction reaction = new CommentReaction();
        reaction.setId(id);
        reaction.setComment(comment);
        reaction.setUserId(userId);
        reaction.setReactionType(type);
        reaction.setCreatedAt(now);
        return reaction;
    }

    @Test
    void givenBlankReaction_whenFieldsAreSet_thenFieldsAreCorrect() {
        CommentReaction reaction = buildReaction(reactionId, ReactionType.UPVOTE);

        assertEquals(reactionId, reaction.getId());
        assertEquals(comment, reaction.getComment());
        assertEquals(userId, reaction.getUserId());
        assertEquals(ReactionType.UPVOTE, reaction.getReactionType());
        assertEquals(now, reaction.getCreatedAt());
    }

    @Test
    void givenAllReactionTypes_whenSet_thenAllValuesAreValid() {
        assertArrayEquals(
                new ReactionType[] {
                        ReactionType.UPVOTE,
                        ReactionType.DOWNVOTE,
                        ReactionType.FIRE,
                        ReactionType.ROCKET,
                        ReactionType.LAUGH,
                        ReactionType.PARTY,
                        ReactionType.THINKING
                },
                ReactionType.values());
    }

    @Test
    void givenTwoReactionsWithSameFields_whenEqualsIsChecked_thenTheyAreEqual() {
        CommentReaction r1 = buildReaction(reactionId, ReactionType.PARTY);
        CommentReaction r2 = buildReaction(reactionId, ReactionType.PARTY);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertTrue(r1.toString().contains("PARTY"));
    }

    @Test
    void givenCommentReactionTable_whenUniqueConstraintInspected_thenUniqueOnCommentAndUserOnly() {
        jakarta.persistence.Table table = CommentReaction.class.getAnnotation(jakarta.persistence.Table.class);
        assertNotNull(table);
        assertEquals(1, table.uniqueConstraints().length);
        jakarta.persistence.UniqueConstraint uc = table.uniqueConstraints()[0];
        assertArrayEquals(new String[] { "comment_id", "user_id" }, uc.columnNames());
    }
}
