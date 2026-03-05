package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionRequestTest {
    @Test
    void testOptionRequestGetterSetter() {
        OptionRequest request = new OptionRequest();

        request.setOptionText("Option 1");
        request.setCorrect(true);

        assertEquals("Option 1", request.getOptionText());
        assertTrue(request.isCorrect());
    }
}