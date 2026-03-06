package id.ac.ui.cs.advprog.yomubackend.quiz.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OptionTest {

    @Test
    void testOptionModel() {
        Option option = new Option();
        Question question = new Question();

        UUID id = UUID.randomUUID();

        option.setId(id);
        option.setOptionText("Java");
        option.setCorrect(true);
        option.setQuestion(question);

        assertEquals(id, option.getId());
        assertEquals("Java", option.getOptionText());
        assertTrue(option.isCorrect());
        assertEquals(question, option.getQuestion());
    }
}