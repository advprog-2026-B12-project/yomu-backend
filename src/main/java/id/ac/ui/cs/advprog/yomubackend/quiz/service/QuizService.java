package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizSubmitRequest;

import java.util.UUID;

public interface QuizService {
    QuizResultResponse submit(UUID userId, QuizSubmitRequest request);
    boolean hasCompleted(UUID userId, UUID readingId);
    void ensureNotCompleted(UUID userId, UUID readingId);
}
