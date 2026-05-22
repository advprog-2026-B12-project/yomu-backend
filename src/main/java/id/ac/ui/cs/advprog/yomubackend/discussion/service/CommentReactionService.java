package id.ac.ui.cs.advprog.yomubackend.discussion.service;

import java.util.Map;
import java.util.UUID;

import id.ac.ui.cs.advprog.yomubackend.discussion.dto.ReactionRequest;
import id.ac.ui.cs.advprog.yomubackend.discussion.entity.ReactionType;

public interface CommentReactionService {

    void addOrUpdateReaction(String username, UUID commentId, ReactionRequest request);

    void removeReaction(String username, UUID commentId);

    Map<ReactionType, Integer> getReactionCounts(UUID commentId);

    ReactionType getUserReaction(String username, UUID commentId);
}
