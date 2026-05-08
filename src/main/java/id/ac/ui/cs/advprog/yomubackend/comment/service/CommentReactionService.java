package id.ac.ui.cs.advprog.yomubackend.comment.service;

import id.ac.ui.cs.advprog.yomubackend.comment.dto.ReactionRequest;
import id.ac.ui.cs.advprog.yomubackend.comment.entity.ReactionType;

import java.util.Map;
import java.util.UUID;

public interface CommentReactionService {

    void addOrUpdateReaction(String username, UUID commentId, ReactionRequest request);

    void removeReaction(String username, UUID commentId);

    Map<ReactionType, Integer> getReactionCounts(UUID commentId);

    ReactionType getUserReaction(String username, UUID commentId);
}
