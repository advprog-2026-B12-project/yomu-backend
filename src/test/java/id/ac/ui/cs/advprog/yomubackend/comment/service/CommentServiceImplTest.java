package id.ac.ui.cs.advprog.yomubackend.comment.service;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.comment.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomubackend.comment.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomubackend.comment.entity.Comment;
import id.ac.ui.cs.advprog.yomubackend.comment.repository.CommentRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ReadingRepository readingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User author;
    private UUID readingId;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(UUID.randomUUID());
        author.setUsername("reader01");

        readingId = UUID.randomUUID();
    }

    private CommentRequest requestWithContent(String content) {
        CommentRequest req = new CommentRequest();
        req.setContent(content);
        return req;
    }

    private Comment savedCommentAnswer() {
        return null; // replaced per-test via when().thenAnswer
    }

    @Test
    void createComment_Success_ReturnsResponseWithAuthorAndReading() {
        when(userRepository.findByUsername("reader01")).thenReturn(Optional.of(author));
        when(readingRepository.existsById(readingId)).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        CommentResponse response = commentService.createComment(
                "reader01", readingId, requestWithContent("hello"));

        assertNotNull(response);
        assertEquals(readingId, response.getReadingId());
        assertEquals(author.getId(), response.getAuthorId());
        assertEquals("hello", response.getContent());
        assertFalse(response.isDeleted());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertNull(response.getEditedAt());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_BlankContent_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                commentService.createComment("reader01", readingId, requestWithContent("   ")));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_ReadingNotFound_ThrowsException() {
        when(userRepository.findByUsername("reader01")).thenReturn(Optional.of(author));
        when(readingRepository.existsById(readingId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                commentService.createComment("reader01", readingId, requestWithContent("hi")));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                commentService.createComment("ghost", readingId, requestWithContent("hi")));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void replyToComment_Success_SetsParentReference() {
        UUID parentId = UUID.randomUUID();
        Comment parent = new Comment();
        parent.setId(parentId);
        parent.setReadingId(readingId);
        parent.setAuthorId(UUID.randomUUID());
        parent.setContent("parent");

        when(userRepository.findByUsername("reader01")).thenReturn(Optional.of(author));
        when(commentRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        CommentResponse response = commentService.replyToComment(
                "reader01", readingId, parentId, requestWithContent("reply"));

        assertNotNull(response);
        assertEquals(parentId, response.getParentId());
        assertEquals(author.getId(), response.getAuthorId());
        assertEquals("reply", response.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void replyToComment_ParentNotFound_ThrowsException() {
        UUID parentId = UUID.randomUUID();
        when(userRepository.findByUsername("reader01")).thenReturn(Optional.of(author));
        when(commentRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                commentService.replyToComment("reader01", readingId, parentId,
                        requestWithContent("reply")));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void replyToComment_ParentDeleted_ThrowsException() {
        UUID parentId = UUID.randomUUID();
        Comment parent = new Comment();
        parent.setId(parentId);
        parent.setReadingId(readingId);
        parent.setDeleted(true);

        when(userRepository.findByUsername("reader01")).thenReturn(Optional.of(author));
        when(commentRepository.findById(parentId)).thenReturn(Optional.of(parent));

        assertThrows(IllegalArgumentException.class, () ->
                commentService.replyToComment("reader01", readingId, parentId,
                        requestWithContent("reply")));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void replyToComment_ParentReadingMismatch_ThrowsException() {
        UUID parentId = UUID.randomUUID();
        Comment parent = new Comment();
        parent.setId(parentId);
        parent.setReadingId(UUID.randomUUID()); // different reading

        when(userRepository.findByUsername("reader01")).thenReturn(Optional.of(author));
        when(commentRepository.findById(parentId)).thenReturn(Optional.of(parent));

        assertThrows(IllegalArgumentException.class, () ->
                commentService.replyToComment("reader01", readingId, parentId,
                        requestWithContent("reply")));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void replyToComment_BlankContent_ThrowsException() {
        UUID parentId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () ->
                commentService.replyToComment("reader01", readingId, parentId,
                        requestWithContent(" ")));

        verify(commentRepository, never()).save(any(Comment.class));
    }
}
