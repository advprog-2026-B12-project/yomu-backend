package id.ac.ui.cs.advprog.yomubackend.discussion.service;

import id.ac.ui.cs.advprog.yomubackend.auth.model.Role;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomubackend.discussion.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomubackend.discussion.entity.Comment;
import id.ac.ui.cs.advprog.yomubackend.discussion.repository.CommentRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    private CommentReactionService reactionService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User author;
    private UUID readingId;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(UUID.randomUUID());
        author.setUsername("reader01");
        author.setRole(Role.PELAJAR);

        readingId = UUID.randomUUID();
    }

    private CommentRequest requestWithContent(String content) {
        CommentRequest req = new CommentRequest();
        req.setContent(content);
        return req;
    }

    @Test
    void createComment_Success_ReturnsResponseWithAuthorAndReading() {
        when(readingRepository.existsById(readingId)).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        CommentResponse response = commentService.createComment(
                author.getId(), readingId, requestWithContent("hello"));

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
        assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(author.getId(), readingId, requestWithContent("   ")));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_ReadingNotFound_ThrowsException() {
        when(readingRepository.existsById(readingId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(author.getId(), readingId, requestWithContent("hi")));

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

        when(commentRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        CommentResponse response = commentService.replyToComment(
                author.getId(), readingId, parentId, requestWithContent("reply"));

        assertNotNull(response);
        assertEquals(parentId, response.getParentId());
        assertEquals(author.getId(), response.getAuthorId());
        assertEquals("reply", response.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void replyToComment_ParentNotFound_ThrowsException() {
        UUID parentId = UUID.randomUUID();
        when(commentRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> commentService.replyToComment(author.getId(), readingId, parentId,
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

        when(commentRepository.findById(parentId)).thenReturn(Optional.of(parent));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.replyToComment(author.getId(), readingId, parentId,
                        requestWithContent("reply")));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void replyToComment_ParentReadingMismatch_ThrowsException() {
        UUID parentId = UUID.randomUUID();
        Comment parent = new Comment();
        parent.setId(parentId);
        parent.setReadingId(UUID.randomUUID()); // different reading

        when(commentRepository.findById(parentId)).thenReturn(Optional.of(parent));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.replyToComment(author.getId(), readingId, parentId,
                        requestWithContent("reply")));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void replyToComment_BlankContent_ThrowsException() {
        UUID parentId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> commentService.replyToComment(author.getId(), readingId, parentId,
                        requestWithContent(" ")));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void replyToComment_MaxDepthExceeded_ThrowsException() {
        UUID topId = UUID.randomUUID();
        UUID replyId = UUID.randomUUID();
        UUID nestedId = UUID.randomUUID();

        Comment top = buildComment(topId, "top", null, false);
        Comment reply = buildComment(replyId, "reply", top, false);
        Comment nested = buildComment(nestedId, "nested", reply, false);

        when(commentRepository.findById(nestedId)).thenReturn(Optional.of(nested));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.replyToComment(author.getId(), readingId, nestedId,
                        requestWithContent("too deep")));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    private Comment buildComment(UUID id, String content, Comment parent, boolean deleted) {
        Comment c = new Comment();
        c.setId(id);
        c.setReadingId(readingId);
        c.setAuthorId(author.getId());
        c.setContent(content);
        c.setCreatedAt(java.time.LocalDateTime.now());
        c.setUpdatedAt(java.time.LocalDateTime.now());
        c.setParent(parent);
        c.setDeleted(deleted);
        return c;
    }

    @Test
    void getCommentsByReadingId_ReturnsNestedTree() {
        Comment top1 = buildComment(UUID.randomUUID(), "top1", null, false);
        Comment top2 = buildComment(UUID.randomUUID(), "top2", null, false);
        Comment reply1 = buildComment(UUID.randomUUID(), "reply1", top1, false);
        Comment reply2 = buildComment(UUID.randomUUID(), "reply2", top1, false);
        Comment nestedReply = buildComment(UUID.randomUUID(), "nested", reply1, false);

        when(commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId))
                .thenReturn(List.of(top1, top2, reply1, reply2, nestedReply));
        when(reactionService.getReactionCounts(any())).thenReturn(java.util.Collections.emptyMap());
        when(reactionService.getUserReaction(eq(author.getId()), any())).thenReturn(null);

        List<CommentResponse> result = commentService.getCommentsByReadingId(readingId, author.getId());

        assertEquals(2, result.size());
        CommentResponse top1Resp = result.stream()
                .filter(r -> r.getId().equals(top1.getId())).findFirst().orElseThrow();
        assertEquals(2, top1Resp.getReplies().size());
        CommentResponse reply1Resp = top1Resp.getReplies().stream()
                .filter(r -> r.getId().equals(reply1.getId())).findFirst().orElseThrow();
        assertEquals(1, reply1Resp.getReplies().size());
        assertEquals(nestedReply.getId(), reply1Resp.getReplies().get(0).getId());
    }

    @Test
    void getCommentsByReadingId_ExcludesSoftDeletedTopLevel() {
        Comment top = buildComment(UUID.randomUUID(), "visible", null, false);
        Comment hidden = buildComment(UUID.randomUUID(), "gone", null, true);

        when(commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId))
                .thenReturn(List.of(top, hidden));
        when(reactionService.getReactionCounts(any())).thenReturn(java.util.Collections.emptyMap());

        List<CommentResponse> result = commentService.getCommentsByReadingId(readingId, null);

        assertEquals(1, result.size());
        assertEquals(top.getId(), result.get(0).getId());
    }

    @Test
    void getCommentsByReadingId_ExcludesSoftDeletedReplies() {
        Comment top = buildComment(UUID.randomUUID(), "visible", null, false);
        Comment replyAlive = buildComment(UUID.randomUUID(), "alive", top, false);
        Comment replyDead = buildComment(UUID.randomUUID(), "dead", top, true);

        when(commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId))
                .thenReturn(List.of(top, replyAlive, replyDead));
        when(reactionService.getReactionCounts(any())).thenReturn(java.util.Collections.emptyMap());

        List<CommentResponse> result = commentService.getCommentsByReadingId(readingId, null);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getReplies().size());
        assertEquals(replyAlive.getId(), result.get(0).getReplies().get(0).getId());
    }

    @Test
    void getCommentsByReadingId_EmptyWhenNoComments() {
        when(commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId))
                .thenReturn(List.of());

        List<CommentResponse> result = commentService.getCommentsByReadingId(readingId, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateComment_Success_SetsEditedAtAndUpdatesContent() {
        UUID commentId = UUID.randomUUID();
        Comment existing = buildComment(commentId, "old content", null, false);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        CommentResponse response = commentService.updateComment(
                author.getId(), readingId, commentId, requestWithContent("new content"));

        assertEquals("new content", response.getContent());
        assertNotNull(response.getEditedAt());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void updateComment_CommentNotFound_ThrowsException() {
        UUID commentId = UUID.randomUUID();
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(author.getId(), readingId, commentId,
                        requestWithContent("x")));
    }

    @Test
    void updateComment_NotAuthor_ThrowsAccessDenied() {
        UUID commentId = UUID.randomUUID();
        Comment existing = buildComment(commentId, "owned by other", null, false);
        existing.setAuthorId(UUID.randomUUID()); // someone else

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> commentService.updateComment(author.getId(), readingId, commentId,
                        requestWithContent("hack")));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void updateComment_ReadingMismatch_ThrowsException() {
        UUID commentId = UUID.randomUUID();
        Comment existing = buildComment(commentId, "content", null, false);
        existing.setReadingId(UUID.randomUUID()); // different reading

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(author.getId(), readingId, commentId,
                        requestWithContent("x")));
    }

    @Test
    void updateComment_AlreadyDeleted_ThrowsException() {
        UUID commentId = UUID.randomUUID();
        Comment existing = buildComment(commentId, "gone", null, true);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(author.getId(), readingId, commentId,
                        requestWithContent("x")));
    }

    @Test
    void updateComment_BlankContent_ThrowsException() {
        UUID commentId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(author.getId(), readingId, commentId,
                        requestWithContent("   ")));
    }

    @Test
    void softDeleteComment_Success_SetsDeletedFlags() {
        UUID commentId = UUID.randomUUID();
        Comment existing = buildComment(commentId, "to delete", null, false);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        commentService.softDeleteComment(author.getId(), readingId, commentId);

        assertTrue(existing.isDeleted());
        assertEquals(author.getId(), existing.getDeletedBy());
        assertNotNull(existing.getDeletedAt());
        verify(commentRepository, times(1)).save(existing);
    }

    @Test
    void softDeleteComment_CommentNotFound_ThrowsException() {
        UUID commentId = UUID.randomUUID();
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> commentService.softDeleteComment(author.getId(), readingId, commentId));
    }

    @Test
    void softDeleteComment_NotAuthor_ThrowsAccessDenied() {
        UUID commentId = UUID.randomUUID();
        Comment existing = buildComment(commentId, "not yours", null, false);
        existing.setAuthorId(UUID.randomUUID());

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> commentService.softDeleteComment(author.getId(), readingId, commentId));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void softDeleteComment_AlreadyDeleted_ThrowsException() {
        UUID commentId = UUID.randomUUID();
        Comment existing = buildComment(commentId, "gone", null, true);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.softDeleteComment(author.getId(), readingId, commentId));
    }

    @Test
    void softDeleteComment_ReadingMismatch_ThrowsException() {
        UUID commentId = UUID.randomUUID();
        Comment existing = buildComment(commentId, "content", null, false);
        existing.setReadingId(UUID.randomUUID());

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.softDeleteComment(author.getId(), readingId, commentId));
    }

    @Test
    void adminDeleteComment_Success_SetsDeletedFlagsByAdmin() {
        UUID commentId = UUID.randomUUID();
        Comment existing = buildComment(commentId, "spam", null, false);
        existing.setAuthorId(UUID.randomUUID());

        User admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setUsername("admin01");
        admin.setRole(Role.ADMIN);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        commentService.adminDeleteComment(admin.getId(), commentId);

        assertTrue(existing.isDeleted());
        assertEquals(admin.getId(), existing.getDeletedBy());
        assertNotNull(existing.getDeletedAt());
        verify(commentRepository, times(1)).save(existing);
    }

    @Test
    void adminDeleteComment_CommentAlreadyDeleted_ThrowsException() {
        UUID commentId = UUID.randomUUID();
        Comment existing = buildComment(commentId, "gone", null, true);

        User admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setUsername("admin01");
        admin.setRole(Role.ADMIN);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.adminDeleteComment(admin.getId(), commentId));
    }

    @Test
    void adminDeleteComment_CommentNotFound_ThrowsException() {
        UUID commentId = UUID.randomUUID();

        User admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setUsername("admin01");
        admin.setRole(Role.ADMIN);

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> commentService.adminDeleteComment(admin.getId(), commentId));
    }
}
