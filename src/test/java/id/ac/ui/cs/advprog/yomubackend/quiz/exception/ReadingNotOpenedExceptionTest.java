package id.ac.ui.cs.advprog.yomubackend.quiz.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReadingNotOpenedExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        ReadingNotOpenedException exception = new ReadingNotOpenedException();

        assertEquals("Reading must be opened before starting quiz", exception.getMessage());
    }
}
