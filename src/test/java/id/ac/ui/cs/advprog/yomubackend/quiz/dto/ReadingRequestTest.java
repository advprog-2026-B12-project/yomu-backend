package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReadingRequestTest {

    @Test
    void testReadingRequestGetterSetter() {
        ReadingRequest request = new ReadingRequest();

        request.setTitle("Sample Title");
        request.setCategory("News & Media");
        request.setContent("Sample Content");

        assertEquals("Sample Title", request.getTitle());
        assertEquals("News & Media", request.getCategory());
        assertEquals("Sample Content", request.getContent());
    }
}
