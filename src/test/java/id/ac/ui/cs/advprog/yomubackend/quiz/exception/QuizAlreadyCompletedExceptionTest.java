package id.ac.ui.cs.advprog.yomubackend.quiz.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizAlreadyCompletedExceptionTest {

    @Test
    void constructor_setsDefaultMessage() {
        QuizAlreadyCompletedException exception = new QuizAlreadyCompletedException();

        assertEquals("Quiz already completed", exception.getMessage());
    }
}
