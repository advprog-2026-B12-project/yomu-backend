package id.ac.ui.cs.advprog.yomubackend.discussion.repository;

import id.ac.ui.cs.advprog.yomubackend.discussion.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.readingId = :readingId")
    void deleteAllByReadingId(@Param("readingId") UUID readingId);

    List<Comment> findByReadingIdAndParentIsNull(UUID readingId);

    List<Comment> findByParentId(UUID parentId);

    List<Comment> findByReadingIdOrderByCreatedAtAsc(UUID readingId);

    @Modifying
    @Query("UPDATE Comment c SET c.deleted = true, c.deletedAt = :deletedAt, c.deletedBy = :userId, c.updatedAt = :deletedAt "
            +
            "WHERE c.authorId = :userId AND c.deleted = false")
    int softDeleteAllByAuthorId(@Param("userId") UUID userId, @Param("deletedAt") LocalDateTime deletedAt);
}
