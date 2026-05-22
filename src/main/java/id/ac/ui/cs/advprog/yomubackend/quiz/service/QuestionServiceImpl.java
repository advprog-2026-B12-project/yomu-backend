package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.exception.QuestionNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.quiz.exception.ReadingNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuestionRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ReadingRepository readingRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               ReadingRepository readingRepository) {
        this.questionRepository = questionRepository;
        this.readingRepository = readingRepository;
    }

    @Override
    public Question create(UUID readingId, Question question) {
        Reading reading = readingRepository.findById(readingId)
                .orElseThrow(() -> new ReadingNotFoundException(readingId));
        question.setReading(reading);
        return questionRepository.save(question);
    }

    @Override
    public Question update(UUID questionId, Question question) {
        Question existing = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));
        existing.setQuestionText(question.getQuestionText());
        return questionRepository.save(existing);
    }

    @Override
    public List<Question> findByReading(UUID readingId) {
        return questionRepository.findByReadingId(readingId);
    }

    @Override
    public void delete(UUID questionId) {
        questionRepository.deleteById(questionId);
    }
}
