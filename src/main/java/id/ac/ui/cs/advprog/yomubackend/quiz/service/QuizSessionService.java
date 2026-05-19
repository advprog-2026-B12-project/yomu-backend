package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import java.util.UUID;

public interface QuizSessionService {
    void start(UUID userId, UUID readingId);
    boolean hasStarted(UUID userId, UUID readingId);
    void ensureNotStarted(UUID userId, UUID readingId);
}
