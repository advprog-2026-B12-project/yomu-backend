package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.exception.ReadingNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizSessionRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingProgressRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReadingServiceImpl implements ReadingService {

    private final ReadingRepository repository;
    private final ReadingProgressRepository readingProgressRepository;
    private final QuizSessionRepository quizSessionRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    public ReadingServiceImpl(ReadingRepository repository,
                              ReadingProgressRepository readingProgressRepository,
                              QuizSessionRepository quizSessionRepository,
                              QuizAttemptRepository quizAttemptRepository) {
        this.repository = repository;
        this.readingProgressRepository = readingProgressRepository;
        this.quizSessionRepository = quizSessionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
    }

    @Override
    public Reading create(Reading reading) {
        return repository.save(reading);
    }

    @Override
    public Reading update(UUID id, Reading reading) {
        Reading existing = findById(id);
        existing.setTitle(reading.getTitle());
        existing.setCategory(reading.getCategory());
        existing.setContent(reading.getContent());
        return repository.save(existing);
    }

    @Override
    public List<Reading> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        readingProgressRepository.deleteByReadingId(id);
        quizSessionRepository.deleteByReadingId(id);
        quizAttemptRepository.deleteByReadingId(id);
        repository.deleteById(id);
    }

    @Override
    public Reading findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ReadingNotFoundException(id));
    }
}
