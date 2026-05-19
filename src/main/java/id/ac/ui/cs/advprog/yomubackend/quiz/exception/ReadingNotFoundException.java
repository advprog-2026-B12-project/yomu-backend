package id.ac.ui.cs.advprog.yomubackend.quiz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReadingNotFoundException extends RuntimeException {
    public ReadingNotFoundException(UUID id) {
        super("Reading not found: " + id);
    }
}
