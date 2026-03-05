package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionRequestTest {
    @Test
    void testQuestionRequestGetterSetter() {
        QuestionRequest request = new QuestionRequest();

        request.setQuestionText("Sample Question?");

        assertEquals("Sample Question?", request.getQuestionText());
    }
}