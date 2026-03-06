package id.ac.ui.cs.advprog.yomubackend.quiz.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReadingTest {

    @Test
    void testReadingModel() {
        Reading reading = new Reading();

        UUID id = UUID.randomUUID();
        reading.setId(id);
        reading.setTitle("Test Title");
        reading.setContent("Test Content");
        reading.setQuestions(new ArrayList<>());

        assertEquals(id, reading.getId());
        assertEquals("Test Title", reading.getTitle());
        assertEquals("Test Content", reading.getContent());
        assertNotNull(reading.getQuestions());
    }
}