package id.ac.ui.cs.advprog.yomubackend.repository;

import id.ac.ui.cs.advprog.yomubackend.entity.CommentReaction;
import id.ac.ui.cs.advprog.yomubackend.entity.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, UUID> {

    List<CommentReaction> findByCommentId(UUID commentId);

    Optional<CommentReaction> findByCommentIdAndUserIdAndReactionType(
            UUID commentId, UUID userId, ReactionType reactionType);

    boolean existsByCommentIdAndUserIdAndReactionType(
            UUID commentId, UUID userId, ReactionType reactionType);
}

