package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.OptionRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Option;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.OptionRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuestionRepository;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;

    public OptionServiceImpl(OptionRepository optionRepository,
                             QuestionRepository questionRepository) {
        this.optionRepository = optionRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Option create(UUID questionId, Option option) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));
        option.setQuestion(question);
        return optionRepository.save(option);
    }

    @Override
    public List<Option> findByQuestion(UUID questionId) {
        return optionRepository.findByQuestionId(questionId);
    }

    @Override
    public Option update(UUID optionId, OptionRequest request) {
        Option existing = optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("Option not found"));
        existing.setOptionText(request.getOptionText());
        existing.setCorrect(request.isCorrect());
        return optionRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID optionId) {
        optionRepository.deleteOptionById(optionId);
    }
}