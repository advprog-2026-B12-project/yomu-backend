package id.ac.ui.cs.advprog.yomubackend.comment.service;

import id.ac.ui.cs.advprog.yomubackend.comment.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomubackend.comment.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomubackend.comment.entity.Comment;
import id.ac.ui.cs.advprog.yomubackend.comment.repository.CommentRepository;
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
    private final UserLookup userLookup;
    private final CommentReactionService reactionService;

    public CommentServiceImpl(CommentRepository commentRepository,
            ReadingRepository readingRepository,
            UserLookup userLookup,
            CommentReactionService reactionService) {
        this.commentRepository = commentRepository;
        this.readingRepository = readingRepository;
        this.userLookup = userLookup;
        this.reactionService = reactionService;
    }

    @Override
    @Transactional
    public CommentResponse createComment(String username, UUID readingId, CommentRequest request) {
        validateContent(request);
        UUID authorId = userLookup.resolveUserId(username);
        if (!readingRepository.existsById(readingId)) {
            throw new IllegalArgumentException("Reading tidak ditemukan!");
        }

        Comment comment = newBaseComment(readingId, authorId, request.getContent().trim());
        Comment saved = commentRepository.save(comment);
        return CommentResponse.fromEntity(saved, Collections.emptyList());
    }

    @Override
    @Transactional
    public CommentResponse replyToComment(String username, UUID readingId, UUID parentCommentId,
            CommentRequest request) {
        validateContent(request);
        UUID authorId = userLookup.resolveUserId(username);

        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Komentar induk tidak ditemukan!"));

        if (parent.isDeleted()) {
            throw new IllegalArgumentException("Komentar induk sudah dihapus!");
        }
        if (!parent.getReadingId().equals(readingId)) {
            throw new IllegalArgumentException("Komentar induk bukan milik reading ini!");
        }

        Comment reply = newBaseComment(readingId, authorId, request.getContent().trim());
        reply.setParent(parent);
        Comment saved = commentRepository.save(reply);
        return CommentResponse.fromEntity(saved, Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByReadingId(UUID readingId, String username) {
        List<Comment> all = commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId);
        return assembleTree(all, username);
    }

    private List<CommentResponse> assembleTree(List<Comment> all, String username) {
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
            result.add(buildNode(top, childrenByParent, username));
        }
        return result;
    }

    private CommentResponse buildNode(Comment comment, Map<UUID, List<Comment>> childrenByParent, String username) {
        List<Comment> children = childrenByParent.getOrDefault(comment.getId(), Collections.emptyList());
        List<CommentResponse> childResponses = new ArrayList<>();
        for (Comment child : children) {
            if (child.isDeleted())
                continue;
            childResponses.add(buildNode(child, childrenByParent, username));
        }
        CommentResponse response = CommentResponse.fromEntity(comment, childResponses);
        response.setReactionCounts(reactionService.getReactionCounts(comment.getId()));
        if (username != null) {
            response.setMyReaction(reactionService.getUserReaction(username, comment.getId()));
        }
        return response;
    }

    @Override
    @Transactional
    public CommentResponse updateComment(String username, UUID readingId, UUID commentId,
            CommentRequest request) {
        validateContent(request);
        UUID currentUserId = userLookup.resolveUserId(username);
        Comment existing = loadActiveCommentForReading(commentId, readingId);
        requireOwnership(existing, currentUserId);

        existing.setContent(request.getContent().trim());
        LocalDateTime now = LocalDateTime.now();
        existing.setUpdatedAt(now);
        existing.setEditedAt(now);

        Comment saved = commentRepository.save(existing);
        return CommentResponse.fromEntity(saved, Collections.emptyList());
    }

    @Override
    @Transactional
    public void softDeleteComment(String username, UUID readingId, UUID commentId) {
        UUID currentUserId = userLookup.resolveUserId(username);
        Comment existing = loadActiveCommentForReading(commentId, readingId);
        requireOwnership(existing, currentUserId);

        LocalDateTime now = LocalDateTime.now();
        existing.setDeleted(true);
        existing.setDeletedBy(currentUserId);
        existing.setDeletedAt(now);
        existing.setUpdatedAt(now);

        commentRepository.save(existing);
    }

    @Override
    @Transactional
    public void adminDeleteComment(String username, UUID commentId) {
        UUID adminId = userLookup.resolveUserId(username);
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
