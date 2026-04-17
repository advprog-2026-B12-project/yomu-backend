package id.ac.ui.cs.advprog.yomubackend.comment.service;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.comment.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomubackend.comment.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomubackend.comment.entity.Comment;
import id.ac.ui.cs.advprog.yomubackend.comment.repository.CommentRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ReadingRepository readingRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              ReadingRepository readingRepository,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.readingRepository = readingRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CommentResponse createComment(String username, UUID readingId, CommentRequest request) {
        validateContent(request);
        User author = resolveUser(username);
        if (!readingRepository.existsById(readingId)) {
            throw new IllegalArgumentException("Reading tidak ditemukan!");
        }

        Comment comment = newBaseComment(readingId, author.getId(), request.getContent().trim());
        Comment saved = commentRepository.save(comment);
        return CommentResponse.fromEntity(saved, Collections.emptyList());
    }

    @Override
    @Transactional
    public CommentResponse replyToComment(String username, UUID readingId, UUID parentCommentId,
                                          CommentRequest request) {
        validateContent(request);
        User author = resolveUser(username);

        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Komentar induk tidak ditemukan!"));

        if (parent.isDeleted()) {
            throw new IllegalArgumentException("Komentar induk sudah dihapus!");
        }
        if (!parent.getReadingId().equals(readingId)) {
            throw new IllegalArgumentException("Komentar induk bukan milik reading ini!");
        }

        Comment reply = newBaseComment(readingId, author.getId(), request.getContent().trim());
        reply.setParent(parent);
        Comment saved = commentRepository.save(reply);
        return CommentResponse.fromEntity(saved, Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByReadingId(UUID readingId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public CommentResponse updateComment(String username, UUID readingId, UUID commentId,
                                         CommentRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public void softDeleteComment(String username, UUID readingId, UUID commentId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private void validateContent(CommentRequest request) {
        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Isi komentar tidak boleh kosong!");
        }
    }

    private User resolveUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan!"));
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
