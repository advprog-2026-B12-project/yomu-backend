package id.ac.ui.cs.advprog.yomubackend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
                        ReactionType.THINKING,
                        ReactionType.CLAP,
                        ReactionType.SURPRISED,
                        ReactionType.LOVE
                },
                ReactionType.values());
    }

    @Test
    void givenTwoReactionsWithSameFields_whenEqualsIsChecked_thenTheyAreEqual() {
        CommentReaction r1 = buildReaction(reactionId, ReactionType.LOVE);
        CommentReaction r2 = buildReaction(reactionId, ReactionType.LOVE);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertTrue(r1.toString().contains("LOVE"));
    }
}
