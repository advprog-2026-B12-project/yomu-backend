package id.ac.ui.cs.advprog.yomubackend.discussion.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.yomubackend.discussion.dto.CommentRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CommentRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        factory.close();
    }

    @Test
    void givenBlankContent_whenValidated_thenConstraintViolation() {
        CommentRequest request = new CommentRequest();
        request.setContent("   ");

        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> "content".equals(v.getPropertyPath().toString())));
    }

    @Test
    void givenNullContent_whenValidated_thenConstraintViolation() {
        CommentRequest request = new CommentRequest();
        request.setContent(null);

        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void givenValidContent_whenValidated_thenNoViolations() {
        CommentRequest request = new CommentRequest();
        request.setContent("a real comment");

        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void givenContentLongerThanMax_whenValidated_thenConstraintViolation() {
        CommentRequest request = new CommentRequest();
        request.setContent("x".repeat(5001));

        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }
}
