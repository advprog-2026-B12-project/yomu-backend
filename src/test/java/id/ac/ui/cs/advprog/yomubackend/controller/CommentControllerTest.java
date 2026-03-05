package id.ac.ui.cs.advprog.yomubackend.controller;

import id.ac.ui.cs.advprog.yomubackend.entity.Comment;
import id.ac.ui.cs.advprog.yomubackend.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentController commentController;

    @Test
    void givenReadingId_whenGetComments_thenReturnEmptyList() {
        UUID readingId = UUID.randomUUID();
        when(commentRepository.findByReadingIdAndParentIsNull(readingId))
                .thenReturn(Collections.emptyList());
        ResponseEntity<List<Comment>> response = commentController.getCommentsByReadingId(readingId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(Collections.emptyList(), response.getBody());
        verify(commentRepository, times(1)).findByReadingIdAndParentIsNull(readingId);
    }

    @Test
    void givenReadingIdWithComments_whenGetComments_thenReturnList() {
        UUID readingId = UUID.randomUUID();
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        List<Comment> mockComments = Arrays.asList(comment1, comment2);

        when(commentRepository.findByReadingIdAndParentIsNull(readingId))
                .thenReturn(mockComments);

        ResponseEntity<List<Comment>> response = commentController.getCommentsByReadingId(readingId);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        
        assertEquals(2, response.getBody().size());
        verify(commentRepository, times(1)).findByReadingIdAndParentIsNull(readingId);
    }
}