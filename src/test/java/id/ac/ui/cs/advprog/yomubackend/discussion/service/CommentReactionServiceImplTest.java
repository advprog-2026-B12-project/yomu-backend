package id.ac.ui.cs.advprog.yomubackend.discussion.service;

import id.ac.ui.cs.advprog.yomubackend.auth.model.Role;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.discussion.dto.ReactionRequest;
import id.ac.ui.cs.advprog.yomubackend.discussion.entity.Comment;
import id.ac.ui.cs.advprog.yomubackend.discussion.entity.CommentReaction;
import id.ac.ui.cs.advprog.yomubackend.discussion.entity.ReactionType;
import id.ac.ui.cs.advprog.yomubackend.discussion.repository.CommentReactionRepository;
import id.ac.ui.cs.advprog.yomubackend.discussion.repository.CommentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentReactionServiceImplTest {

    @Mock
    private CommentReactionRepository reactionRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentReactionServiceImpl reactionService;

    private User user;
    private UUID commentId;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("reader01");
        user.setRole(Role.PELAJAR);

        commentId = UUID.randomUUID();
    }

    private ReactionRequest requestWithType(ReactionType type) {
        ReactionRequest req = new ReactionRequest();
        req.setReactionType(type);
        return req;
    }

    @Test
    void addOrUpdateReaction_NewReaction_SavesToRepository() {
        Comment comment = new Comment();
        comment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(reactionRepository.findByCommentIdAndUserId(commentId, user.getId())).thenReturn(Optional.empty());
        when(reactionRepository.save(any(CommentReaction.class))).thenAnswer(inv -> {
            CommentReaction r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        reactionService.addOrUpdateReaction(user.getId(), commentId, requestWithType(ReactionType.UPVOTE));

        verify(reactionRepository, times(1)).save(any(CommentReaction.class));
    }

    @Test
    void addOrUpdateReaction_ExistingReaction_UpdatesType() {
        Comment comment = new Comment();
        comment.setId(commentId);
        CommentReaction existing = new CommentReaction();
        existing.setComment(comment);
        existing.setUserId(user.getId());
        existing.setReactionType(ReactionType.UPVOTE);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(reactionRepository.findByCommentIdAndUserId(commentId, user.getId())).thenReturn(Optional.of(existing));

        reactionService.addOrUpdateReaction(user.getId(), commentId, requestWithType(ReactionType.DOWNVOTE));

        assertEquals(ReactionType.DOWNVOTE, existing.getReactionType());
        verify(reactionRepository, never()).save(any(CommentReaction.class));
    }

    @Test
    void addOrUpdateReaction_CommentNotFound_ThrowsException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> reactionService.addOrUpdateReaction(user.getId(), commentId,
                        requestWithType(ReactionType.UPVOTE)));
    }

    @Test
    void removeReaction_Existing_DeletesFromRepository() {
        CommentReaction existing = new CommentReaction();
        existing.setId(UUID.randomUUID());

        when(reactionRepository.findByCommentIdAndUserId(commentId, user.getId())).thenReturn(Optional.of(existing));

        reactionService.removeReaction(user.getId(), commentId);

        verify(reactionRepository, times(1)).delete(existing);
    }

    @Test
    void removeReaction_NonExisting_DoesNothing() {
        when(reactionRepository.findByCommentIdAndUserId(commentId, user.getId())).thenReturn(Optional.empty());

        reactionService.removeReaction(user.getId(), commentId);

        verify(reactionRepository, never()).delete(any());
    }

    @Test
    void getReactionCounts_AggregatesByType() {
        CommentReaction r1 = new CommentReaction();
        r1.setReactionType(ReactionType.UPVOTE);
        CommentReaction r2 = new CommentReaction();
        r2.setReactionType(ReactionType.UPVOTE);
        CommentReaction r3 = new CommentReaction();
        r3.setReactionType(ReactionType.DOWNVOTE);

        when(reactionRepository.findByCommentId(commentId)).thenReturn(List.of(r1, r2, r3));

        var counts = reactionService.getReactionCounts(commentId);

        assertEquals(2, counts.get(ReactionType.UPVOTE));
        assertEquals(1, counts.get(ReactionType.DOWNVOTE));
    }

    @Test
    void getUserReaction_ReturnsTypeWhenExists() {
        CommentReaction r = new CommentReaction();
        r.setReactionType(ReactionType.FIRE);

        when(reactionRepository.findByCommentIdAndUserId(commentId, user.getId())).thenReturn(Optional.of(r));

        ReactionType result = reactionService.getUserReaction(user.getId(), commentId);

        assertEquals(ReactionType.FIRE, result);
    }

    @Test
    void getUserReaction_ReturnsNullWhenNotExists() {
        when(reactionRepository.findByCommentIdAndUserId(commentId, user.getId())).thenReturn(Optional.empty());

        ReactionType result = reactionService.getUserReaction(user.getId(), commentId);

        assertNull(result);
    }
}
