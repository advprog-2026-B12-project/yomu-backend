package id.ac.ui.cs.advprog.yomubackend.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import id.ac.ui.cs.advprog.yomubackend.comment.entity.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findByReadingIdAndParentIsNull(UUID readingId);

    List<Comment> findByParentId(UUID parentId);
}
