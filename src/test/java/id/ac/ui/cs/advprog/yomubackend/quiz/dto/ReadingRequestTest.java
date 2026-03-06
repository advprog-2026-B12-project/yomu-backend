package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReadingRequestTest {

    @Test
    void testReadingRequestGetterSetter() {
        ReadingRequest request = new ReadingRequest();

        request.setTitle("Sample Title");
        request.setContent("Sample Content");

        assertEquals("Sample Title", request.getTitle());
        assertEquals("Sample Content", request.getContent());
    }
}