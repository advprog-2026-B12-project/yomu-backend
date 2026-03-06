package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReadingServiceImpl implements ReadingService {

    private final ReadingRepository repository;

    public ReadingServiceImpl(ReadingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Reading create(Reading reading) {
        return repository.save(reading);
    }

    @Override
    public List<Reading> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
