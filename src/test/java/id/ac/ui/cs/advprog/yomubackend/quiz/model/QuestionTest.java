package id.ac.ui.cs.advprog.yomubackend.quiz.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    void testQuestionModel() {
        Question question = new Question();
        Reading reading = new Reading();

        UUID id = UUID.randomUUID();

        question.setId(id);
        question.setQuestionText("What is Java?");
        question.setReading(reading);
        question.setOptions(new ArrayList<>());

        assertEquals(id, question.getId());
        assertEquals("What is Java?", question.getQuestionText());
        assertEquals(reading, question.getReading());
        assertNotNull(question.getOptions());
    }
}