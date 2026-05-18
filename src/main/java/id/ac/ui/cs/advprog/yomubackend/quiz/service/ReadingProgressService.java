package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import java.util.UUID;

public interface ReadingProgressService {
    void markOpened(UUID userId, UUID readingId);
    void ensureOpened(UUID userId, UUID readingId);
}
