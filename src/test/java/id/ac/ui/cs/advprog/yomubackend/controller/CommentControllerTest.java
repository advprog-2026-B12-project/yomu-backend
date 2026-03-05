package id.ac.ui.cs.advprog.yomubackend.controller;

import id.ac.ui.cs.advprog.yomubackend.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        ResponseEntity<List<?>> response = commentController.getCommentsByReadingId(readingId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(Collections.emptyList(), response.getBody());
        verify(commentRepository, times(1)).findByReadingIdAndParentIsNull(readingId);
    }

    @Test
    void givenReadingIdWithComments_whenGetComments_thenReturnList() {
        UUID readingId = UUID.randomUUID();
        when(commentRepository.findByReadingIdAndParentIsNull(readingId))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<?>> response = commentController.getCommentsByReadingId(readingId);

        assertEquals(200, response.getStatusCode().value());
        verify(commentRepository, times(1)).findByReadingIdAndParentIsNull(readingId);
    }
}

