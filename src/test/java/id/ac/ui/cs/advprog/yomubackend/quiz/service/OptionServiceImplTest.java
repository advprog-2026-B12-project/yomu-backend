package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Option;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.OptionRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuestionRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OptionServiceImplTest {

    OptionRepository optionRepo = mock(OptionRepository.class);
    QuestionRepository questionRepo = mock(QuestionRepository.class);

    OptionServiceImpl service = new OptionServiceImpl(optionRepo, questionRepo);

    @Test
    void testCreateOption() {
        UUID questionId = UUID.randomUUID();

        Question question = new Question();
        question.setId(questionId);

        Option option = new Option();

        when(questionRepo.findById(questionId)).thenReturn(Optional.of(question));
        when(optionRepo.save(option)).thenReturn(option);

        Option result = service.create(questionId, option);

        assertEquals(question, result.getQuestion());
    }

    @Test
    void testCreateOptionQuestionNotFound() {
        UUID id = UUID.randomUUID();
        Option option = new Option();

        when(questionRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.create(id, option));
    }

    @Test
    void testFindByQuestion() {
        UUID questionId = UUID.randomUUID();

        Question q = new Question();
        q.setId(questionId);

        Option o = new Option();
        o.setQuestion(q);

        when(optionRepo.findByQuestionId(questionId)).thenReturn(List.of(o));

        List<Option> result = optionRepo.findByQuestionId(questionId);

        assertEquals(1, result.size());
    }

    @Test
    void testDeleteOption() {
        UUID id = UUID.randomUUID();

        service.delete(id);

        verify(optionRepo).deleteById(id);
    }
}