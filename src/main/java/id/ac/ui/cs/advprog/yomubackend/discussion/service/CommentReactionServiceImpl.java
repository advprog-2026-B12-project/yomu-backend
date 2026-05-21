package id.ac.ui.cs.advprog.yomubackend.discussion.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.ac.ui.cs.advprog.yomubackend.discussion.dto.ReactionRequest;
import id.ac.ui.cs.advprog.yomubackend.discussion.entity.Comment;
import id.ac.ui.cs.advprog.yomubackend.discussion.entity.CommentReaction;
import id.ac.ui.cs.advprog.yomubackend.discussion.entity.ReactionType;
import id.ac.ui.cs.advprog.yomubackend.discussion.repository.CommentReactionRepository;
import id.ac.ui.cs.advprog.yomubackend.discussion.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CommentReactionServiceImpl implements CommentReactionService {

    private final CommentReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
    private final UserLookup userLookup;

    public CommentReactionServiceImpl(CommentReactionRepository reactionRepository,
            CommentRepository commentRepository,
            UserLookup userLookup) {
        this.reactionRepository = reactionRepository;
        this.commentRepository = commentRepository;
        this.userLookup = userLookup;
    }

    @Override
    @Transactional
    public void addOrUpdateReaction(String username, UUID commentId, ReactionRequest request) {
        UUID userId = userLookup.resolveUserId(username);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Komentar tidak ditemukan!"));

        reactionRepository.findByCommentIdAndUserId(commentId, userId)
                .ifPresentOrElse(
                        existing -> {
                            existing.setReactionType(request.getReactionType());
                        },
                        () -> {
                            CommentReaction reaction = new CommentReaction();
                            reaction.setComment(comment);
                            reaction.setUserId(userId);
                            reaction.setReactionType(request.getReactionType());
                            reaction.setCreatedAt(LocalDateTime.now());
                            reactionRepository.save(reaction);
                        });
    }

    @Override
    @Transactional
    public void removeReaction(String username, UUID commentId) {
        UUID userId = userLookup.resolveUserId(username);
        reactionRepository.findByCommentIdAndUserId(commentId, userId)
                .ifPresent(reactionRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<ReactionType, Integer> getReactionCounts(UUID commentId) {
        List<CommentReaction> reactions = reactionRepository.findByCommentId(commentId);
        Map<ReactionType, Integer> counts = new EnumMap<>(ReactionType.class);
        for (CommentReaction r : reactions) {
            counts.merge(r.getReactionType(), 1, Integer::sum);
        }
        return counts;
    }

    @Override
    @Transactional(readOnly = true)
    public ReactionType getUserReaction(String username, UUID commentId) {
        UUID userId = userLookup.resolveUserId(username);
        return reactionRepository.findByCommentIdAndUserId(commentId, userId)
                .map(CommentReaction::getReactionType)
                .orElse(null);
    }
}
