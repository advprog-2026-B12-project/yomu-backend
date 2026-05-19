package id.ac.ui.cs.advprog.yomubackend.quiz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class QuizAlreadyStartedException extends RuntimeException {
    public QuizAlreadyStartedException() {
        super("Quiz already started");
    }
}
