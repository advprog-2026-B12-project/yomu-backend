package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.exception.ReadingNotOpenedException;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.ReadingProgress;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReadingProgressServiceImpl implements ReadingProgressService {

    private final ReadingProgressRepository readingProgressRepository;

    public ReadingProgressServiceImpl(ReadingProgressRepository readingProgressRepository) {
        this.readingProgressRepository = readingProgressRepository;
    }

    @Override
    @Transactional
    public void markOpened(UUID userId, UUID readingId) {
        if (readingProgressRepository.existsByUserIdAndReadingId(userId, readingId)) {
            return;
        }

        ReadingProgress progress = new ReadingProgress();
        progress.setUserId(userId);
        progress.setReadingId(readingId);
        progress.setOpenedAt(LocalDateTime.now());

        readingProgressRepository.save(progress);
    }

    @Override
    public void ensureOpened(UUID userId, UUID readingId) {
        if (!readingProgressRepository.existsByUserIdAndReadingId(userId, readingId)) {
            throw new ReadingNotOpenedException();
        }
    }
}
