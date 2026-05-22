package id.ac.ui.cs.advprog.yomubackend.discussion;

import id.ac.ui.cs.advprog.yomubackend.discussion.entity.Comment;
import id.ac.ui.cs.advprog.yomubackend.discussion.repository.CommentRepository;
import id.ac.ui.cs.advprog.yomubackend.discussion.service.CommentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SpringBootTest
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
        // Reduced to 50 top-level and 5 replies (550 comments total) to prevent the
        // test
        // from hanging for 10+ minutes when testing the N+1 problem against a real
        // Docker database.
        // Even with this amount, the N+1 problem will be clearly visible in the
        // profiling results.
        testReadingId = dataGenerator.generateTestData(50, 5);
    }

    @AfterEach
    void tearDown() {
        if (testReadingId != null) {
            // Delete generated comments from the database to avoid polluting it
            // We reverse the list to delete nested replies (children) first before their
            // parents
            List<Comment> comments = commentRepository.findByReadingIdOrderByCreatedAtAsc(testReadingId);
            Collections.reverse(comments);
            commentRepository.deleteAll(comments);
        }
    }

    @Test
    void profile_BEFORE_optimization() {
        warmup();

        System.gc();
        try {
            Thread.sleep(500); // Give GC time to run for a cleaner baseline
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int iterations = 20; // 20 iterations is enough to see the average time reliably without taking too
                             // long
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
