package id.ac.ui.cs.advprog.yomubackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(
        name = "comment_reactions",
        uniqueConstraints = @UniqueConstraint(
                name = "UQ_reaction_comment_user_type",
                columnNames = {"comment_id", "user_id", "reaction_type"}
        ),
        indexes = {
                @Index(name = "IDX_reactions_comment_id",   columnList = "comment_id"),
                @Index(name = "IDX_reactions_user_comment", columnList = "user_id, comment_id")
        }
)
public class CommentReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, length = 20)
    private ReactionType reactionType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

