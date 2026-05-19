package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReadingResponseTest {

    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();

        ReadingResponse response = ReadingResponse.builder()
                .id(id)
                .title("Title")
                .category("News & Media")
                .content("Content")
                .build();

        assertEquals(id, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals("News & Media", response.getCategory());
        assertEquals("Content", response.getContent());
    }
}
