package id.ac.ui.cs.advprog.yomubackend.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import id.ac.ui.cs.advprog.yomubackend.comment.entity.CommentReaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, UUID> {

        List<CommentReaction> findByCommentId(UUID commentId);

        Optional<CommentReaction> findByCommentIdAndUserId(UUID commentId, UUID userId);

        void deleteByCommentIdAndUserId(UUID commentId, UUID userId);
}
