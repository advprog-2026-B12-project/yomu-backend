package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;

import java.util.List;
import java.util.UUID;

public interface QuestionService {
    Question create(UUID readingId, Question question);
    List<Question> findByReading(UUID readingId);
    void delete(UUID questionId);
}