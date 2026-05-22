package id.ac.ui.cs.advprog.yomubackend.discussion;

import id.ac.ui.cs.advprog.yomubackend.discussion.entity.Comment;
import id.ac.ui.cs.advprog.yomubackend.discussion.repository.CommentRepository;
import id.ac.ui.cs.advprog.yomubackend.discussion.service.CommentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
class CommentServiceProfilingTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProfileTestDataGenerator dataGenerator;

    private UUID testReadingId;

    @BeforeEach
    void setUp() {
        testReadingId = dataGenerator.generateTestData(50, 5);
    }

    @AfterEach
    void tearDown() {
        if (testReadingId != null) {
            List<Comment> comments = commentRepository.findByReadingIdOrderByCreatedAtAsc(testReadingId);
            Collections.reverse(comments);
            commentRepository.deleteAll(comments);
        }
    }

    @Test
    void profile_optimization() {
        warmup();

        System.gc();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int iterations = 20;
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            commentService.getCommentsByReadingId(testReadingId, null);
        }
        long duration = System.nanoTime() - start;

        System.out.printf("BEFORE: Avg = %.2f ms%n", duration / (double) iterations / 1_000_000.0);
    }

    private void warmup() {
        for (int i = 0; i < 5; i++) {
            commentService.getCommentsByReadingId(testReadingId, null);
        }
    }
}