package id.ac.ui.cs.advprog.yomubackend.quiz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ReadingNotOpenedException extends RuntimeException {
    public ReadingNotOpenedException() {
        super("Reading must be opened before starting quiz");
    }
}
