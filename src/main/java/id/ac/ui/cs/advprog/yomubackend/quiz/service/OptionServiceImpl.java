package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Option;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.OptionRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuestionRepository;
import org.springframework.stereotype.Service;

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
        return optionRepository.findAll()
                .stream()
                .filter(o -> o.getQuestion().getId().equals(questionId))
                .toList();
    }

    @Override
    public void delete(UUID optionId) {
        optionRepository.deleteById(optionId);
    }
}
