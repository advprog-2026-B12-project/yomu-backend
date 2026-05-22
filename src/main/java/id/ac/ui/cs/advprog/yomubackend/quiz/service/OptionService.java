package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.OptionRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Option;

import java.util.List;
import java.util.UUID;

public interface OptionService {
    Option create(UUID questionId, Option option);
    List<Option> findByQuestion(UUID questionId);
    Option update(UUID optionId, OptionRequest request);
    void delete(UUID optionId);
}
