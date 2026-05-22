package id.ac.ui.cs.advprog.yomubackend.discussion.service;

import id.ac.ui.cs.advprog.yomubackend.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomubackend.discussion.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomubackend.discussion.entity.Comment;
import id.ac.ui.cs.advprog.yomubackend.discussion.repository.CommentRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ReadingRepository readingRepository;
    private final CommentReactionService reactionService;

    public CommentServiceImpl(CommentRepository commentRepository,
            ReadingRepository readingRepository,
            CommentReactionService reactionService) {
        this.commentRepository = commentRepository;
        this.readingRepository = readingRepository;
        this.reactionService = reactionService;
    }

    @Override
    @Transactional
    public CommentResponse createComment(UUID userId, UUID readingId, CommentRequest request) {
        validateContent(request);
        if (!readingRepository.existsById(readingId)) {
            throw new IllegalArgumentException("Reading tidak ditemukan!");
        }

        Comment comment = newBaseComment(readingId, userId, request.getContent().trim());
        Comment saved = commentRepository.save(comment);
        return CommentResponse.fromEntity(saved, Collections.emptyList());
    }

    @Override
    @Transactional
    public CommentResponse replyToComment(UUID userId, UUID readingId, UUID parentCommentId,
            CommentRequest request) {
        validateContent(request);

        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Komentar induk tidak ditemukan!"));

        if (parent.isDeleted()) {
            throw new IllegalArgumentException("Komentar induk sudah dihapus!");
        }
        if (!parent.getReadingId().equals(readingId)) {
            throw new IllegalArgumentException("Komentar induk bukan milik reading ini!");
        }

        int parentDepth = calculateDepth(parent);
        if (parentDepth >= 2) {
            throw new IllegalArgumentException("Balasan komentar maksimal 3 tingkat!");
        }

        Comment reply = newBaseComment(readingId, userId, request.getContent().trim());
        reply.setParent(parent);
        Comment saved = commentRepository.save(reply);
        return CommentResponse.fromEntity(saved, Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByReadingId(UUID readingId, UUID userId) {
        List<Comment> all = commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId);

        List<UUID> commentIds = all.stream().map(Comment::getId).toList();
        Map<UUID, Map<id.ac.ui.cs.advprog.yomubackend.discussion.entity.ReactionType, Integer>> bulkCounts = reactionService
                .getBulkReactionCounts(commentIds);
        Map<UUID, id.ac.ui.cs.advprog.yomubackend.discussion.entity.ReactionType> bulkUserReactions = reactionService
                .getBulkUserReactions(userId, commentIds);

        return assembleTree(all, userId, bulkCounts, bulkUserReactions);
    }

    private List<CommentResponse> assembleTree(List<Comment> all, UUID userId,
            Map<UUID, Map<id.ac.ui.cs.advprog.yomubackend.discussion.entity.ReactionType, Integer>> bulkCounts,
            Map<UUID, id.ac.ui.cs.advprog.yomubackend.discussion.entity.ReactionType> bulkUserReactions) {
        Map<UUID, List<Comment>> childrenByParent = new HashMap<>();
        List<Comment> topLevel = new ArrayList<>();
        for (Comment c : all) {
            if (c.getParent() == null) {
                topLevel.add(c);
            } else {
                childrenByParent
                        .computeIfAbsent(c.getParent().getId(), k -> new ArrayList<>())
                        .add(c);
            }
        }
        List<CommentResponse> result = new ArrayList<>();
        for (Comment top : topLevel) {
            if (top.isDeleted())
                continue;
            result.add(buildNode(top, childrenByParent, userId, 0, bulkCounts, bulkUserReactions));
        }
        return result;
    }

    private CommentResponse buildNode(Comment comment, Map<UUID, List<Comment>> childrenByParent,
            UUID userId, int depth,
            Map<UUID, Map<id.ac.ui.cs.advprog.yomubackend.discussion.entity.ReactionType, Integer>> bulkCounts,
            Map<UUID, id.ac.ui.cs.advprog.yomubackend.discussion.entity.ReactionType> bulkUserReactions) {
        List<Comment> children = childrenByParent.getOrDefault(comment.getId(), Collections.emptyList());
        List<CommentResponse> childResponses = new ArrayList<>();
        if (depth < 2) {
            for (Comment child : children) {
                if (child.isDeleted())
                    continue;
                childResponses
                        .add(buildNode(child, childrenByParent, userId, depth + 1, bulkCounts, bulkUserReactions));
            }
        }
        CommentResponse response = CommentResponse.fromEntity(comment, childResponses);
        response.setReactionCounts(bulkCounts.getOrDefault(comment.getId(), Collections.emptyMap()));
        if (userId != null) {
            response.setMyReaction(bulkUserReactions.get(comment.getId()));
        }
        return response;
    }

    @Override
    @Transactional
    public CommentResponse updateComment(UUID userId, UUID readingId, UUID commentId,
            CommentRequest request) {
        validateContent(request);
        Comment existing = loadActiveCommentForReading(commentId, readingId);
        requireOwnership(existing, userId);

        existing.setContent(request.getContent().trim());
        LocalDateTime now = LocalDateTime.now();
        existing.setUpdatedAt(now);
        existing.setEditedAt(now);

        Comment saved = commentRepository.save(existing);
        return CommentResponse.fromEntity(saved, Collections.emptyList());
    }

    @Override
    @Transactional
    public void softDeleteComment(UUID userId, UUID readingId, UUID commentId) {
        Comment existing = loadActiveCommentForReading(commentId, readingId);
        requireOwnership(existing, userId);

        LocalDateTime now = LocalDateTime.now();
        existing.setDeleted(true);
        existing.setDeletedBy(userId);
        existing.setDeletedAt(now);
        existing.setUpdatedAt(now);

        commentRepository.save(existing);
    }

    @Override
    @Transactional
    public void adminDeleteComment(UUID adminId, UUID commentId) {
        Comment existing = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Komentar tidak ditemukan!"));
        if (existing.isDeleted()) {
            throw new IllegalArgumentException("Komentar sudah dihapus!");
        }

        LocalDateTime now = LocalDateTime.now();
        existing.setDeleted(true);
        existing.setDeletedBy(adminId);
        existing.setDeletedAt(now);
        existing.setUpdatedAt(now);

        commentRepository.save(existing);
    }

    private Comment loadActiveCommentForReading(UUID commentId, UUID readingId) {
        Comment existing = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Komentar tidak ditemukan!"));
        if (!existing.getReadingId().equals(readingId)) {
            throw new IllegalArgumentException("Komentar bukan milik reading ini!");
        }
        if (existing.isDeleted()) {
            throw new IllegalArgumentException("Komentar sudah dihapus!");
        }
        return existing;
    }

    private void requireOwnership(Comment comment, UUID userId) {
        if (!comment.getAuthorId().equals(userId)) {
            throw new AccessDeniedException("Anda tidak memiliki akses untuk komentar ini!");
        }
    }

    private void validateContent(CommentRequest request) {
        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Isi komentar tidak boleh kosong!");
        }
    }

    private int calculateDepth(Comment comment) {
        int depth = 0;
        Comment current = comment;
        while (current.getParent() != null) {
            depth++;
            current = current.getParent();
        }
        return depth;
    }

    private Comment newBaseComment(UUID readingId, UUID authorId, String content) {
        Comment comment = new Comment();
        comment.setReadingId(readingId);
        comment.setAuthorId(authorId);
        comment.setContent(content);
        LocalDateTime now = LocalDateTime.now();
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);
        comment.setDeleted(false);
        return comment;
    }
}
