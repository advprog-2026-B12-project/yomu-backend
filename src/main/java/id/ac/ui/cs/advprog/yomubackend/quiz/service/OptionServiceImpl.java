package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.exception.OptionNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.quiz.exception.QuestionNotFoundException;
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
                .orElseThrow(() -> new QuestionNotFoundException(questionId));
        if (option.isCorrect()) {
            clearOtherCorrectOptions(questionId, null);
        }
        option.setQuestion(question);
        return optionRepository.save(option);
    }

    @Override
    public Option update(UUID optionId, Option option) {
        Option existing = optionRepository.findById(optionId)
                .orElseThrow(() -> new OptionNotFoundException(optionId));

        existing.setOptionText(option.getOptionText());
        existing.setCorrect(option.isCorrect());

        if (existing.isCorrect()) {
            clearOtherCorrectOptions(existing.getQuestion().getId(), existing.getId());
        }

        return optionRepository.save(existing);
    }

    @Override
    public List<Option> findByQuestion(UUID questionId) {
        return optionRepository.findByQuestionId(questionId);
    }

    @Override
    public void delete(UUID optionId) {
        optionRepository.deleteById(optionId);
    }

    private void clearOtherCorrectOptions(UUID questionId, UUID ignoredOptionId) {
        List<Option> options = optionRepository.findByQuestionId(questionId);
        options.stream()
                .filter(Option::isCorrect)
                .filter(option -> ignoredOptionId == null || !ignoredOptionId.equals(option.getId()))
                .forEach(option -> option.setCorrect(false));
        optionRepository.saveAll(options);
    }
}
