package id.ac.ui.cs.advprog.yomubackend.quiz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class QuizAlreadyCompletedException extends RuntimeException {
    public QuizAlreadyCompletedException() {
        super("Quiz already completed");
    }
}
