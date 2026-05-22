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
import java.util.Collections;

@Service
public class CommentReactionServiceImpl implements CommentReactionService {

    private final CommentReactionRepository reactionRepository;
    private final CommentRepository commentRepository;

    public CommentReactionServiceImpl(CommentReactionRepository reactionRepository,
            CommentRepository commentRepository) {
        this.reactionRepository = reactionRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public void addOrUpdateReaction(UUID userId, UUID commentId, ReactionRequest request) {
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
    public void removeReaction(UUID userId, UUID commentId) {
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
    public ReactionType getUserReaction(UUID userId, UUID commentId) {
        return reactionRepository.findByCommentIdAndUserId(commentId, userId)
                .map(CommentReaction::getReactionType)
                .orElse(null);
    }
    @Override
    @Transactional(readOnly = true)
    public Map<UUID, Map<ReactionType, Integer>> getBulkReactionCounts(List<UUID> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<CommentReaction> reactions = reactionRepository.findByCommentIdIn(commentIds);
        Map<UUID, Map<ReactionType, Integer>> result = new java.util.HashMap<>();

        for (CommentReaction r : reactions) {
            UUID cId = r.getComment().getId();
            result.computeIfAbsent(cId, k -> new EnumMap<>(ReactionType.class))
                  .merge(r.getReactionType(), 1, Integer::sum);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, ReactionType> getBulkUserReactions(UUID userId, List<UUID> commentIds) {
        if (userId == null || commentIds == null || commentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<CommentReaction> reactions = reactionRepository.findByUserIdAndCommentIdIn(userId, commentIds);
        Map<UUID, ReactionType> result = new java.util.HashMap<>();

        for (CommentReaction r : reactions) {
            result.put(r.getComment().getId(), r.getReactionType());
        }
        return result;
    }

}
