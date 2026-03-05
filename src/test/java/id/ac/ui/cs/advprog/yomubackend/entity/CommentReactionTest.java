package id.ac.ui.cs.advprog.yomubackend.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommentReactionTest {

    @Test
    void givenBlankReaction_whenFieldsAreSet_thenFieldsAreCorrect() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());

        CommentReaction reaction = new CommentReaction();
        reaction.setId(id);
        reaction.setComment(comment);
        reaction.setUserId(userId);
        reaction.setReactionType(ReactionType.UPVOTE);
        reaction.setCreatedAt(now);

        assertEquals(id, reaction.getId());
        assertEquals(comment, reaction.getComment());
        assertEquals(userId, reaction.getUserId());
        assertEquals(ReactionType.UPVOTE, reaction.getReactionType());
        assertEquals(now, reaction.getCreatedAt());
    }

    @Test
    void givenAllReactionTypes_whenSet_thenAllValuesAreValid() {
        ReactionType[] types = ReactionType.values();

        assertArrayEquals(
                new ReactionType[]{
                        ReactionType.UPVOTE,
                        ReactionType.DOWNVOTE,
                        ReactionType.FIRE,
                        ReactionType.THINKING,
                        ReactionType.CLAP,
                        ReactionType.SURPRISED,
                        ReactionType.LOVE
                },
                types
        );
    }

    @Test
    void givenTwoReactionsWithSameFields_whenEqualsIsChecked_thenTheyAreEqual() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());

        CommentReaction r1 = new CommentReaction();
        r1.setId(id);
        r1.setComment(comment);
        r1.setUserId(userId);
        r1.setReactionType(ReactionType.LOVE);
        r1.setCreatedAt(now);

        CommentReaction r2 = new CommentReaction();
        r2.setId(id);
        r2.setComment(comment);
        r2.setUserId(userId);
        r2.setReactionType(ReactionType.LOVE);
        r2.setCreatedAt(now);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertTrue(r1.toString().contains("LOVE"));
    }
}

