package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.exception.QuizAlreadyStartedException;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.QuizSession;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class QuizSessionServiceImpl implements QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;

    public QuizSessionServiceImpl(QuizSessionRepository quizSessionRepository) {
        this.quizSessionRepository = quizSessionRepository;
    }

    @Override
    @Transactional
    public void start(UUID userId, UUID readingId) {
        if (hasStarted(userId, readingId)) {
            return;
        }

        QuizSession session = new QuizSession();
        session.setUserId(userId);
        session.setReadingId(readingId);
        session.setStartedAt(LocalDateTime.now());

        quizSessionRepository.save(session);
    }

    @Override
    public boolean hasStarted(UUID userId, UUID readingId) {
        return quizSessionRepository.existsByUserIdAndReadingId(userId, readingId);
    }

    @Override
    public void ensureNotStarted(UUID userId, UUID readingId) {
        if (hasStarted(userId, readingId)) {
            throw new QuizAlreadyStartedException();
        }
    }
}
