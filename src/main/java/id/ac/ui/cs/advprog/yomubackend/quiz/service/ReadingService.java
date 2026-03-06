package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;

import java.util.List;
import java.util.UUID;

public interface ReadingService {
    Reading create(Reading reading);
    List<Reading> findAll();
    void delete(UUID id);
}