package id.ac.ui.cs.advprog.yomubackend.discussion.service;

import java.util.Map;
import java.util.UUID;

import id.ac.ui.cs.advprog.yomubackend.discussion.dto.ReactionRequest;
import id.ac.ui.cs.advprog.yomubackend.discussion.entity.ReactionType;

public interface CommentReactionService {

    void addOrUpdateReaction(UUID userId, UUID commentId, ReactionRequest request);

    void removeReaction(UUID userId, UUID commentId);

    Map<ReactionType, Integer> getReactionCounts(UUID commentId);

    ReactionType getUserReaction(UUID userId, UUID commentId);
}
