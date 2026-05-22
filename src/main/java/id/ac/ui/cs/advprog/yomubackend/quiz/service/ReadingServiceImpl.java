package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.exception.ReadingNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.quiz.ReadingDeletedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReadingServiceImpl implements ReadingService {

    private final ReadingRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public ReadingServiceImpl(ReadingRepository repository,
                              ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
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
        if (!repository.existsById(id)) {
            throw new ReadingNotFoundException(id);
        }
        repository.deleteById(id);
        eventPublisher.publishEvent(new ReadingDeletedEvent(id, LocalDateTime.now()));
    }

    @Override
    public Reading findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ReadingNotFoundException(id));
    }
}
