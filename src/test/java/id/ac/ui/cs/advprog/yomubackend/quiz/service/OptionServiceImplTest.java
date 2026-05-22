package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.exception.OptionNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.quiz.exception.QuestionNotFoundException;
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
    void createCorrectOption_shouldClearExistingCorrectOptions() {
        UUID questionId = UUID.randomUUID();

        Question question = new Question();
        question.setId(questionId);

        Option existingCorrect = new Option();
        existingCorrect.setId(UUID.randomUUID());
        existingCorrect.setCorrect(true);
        existingCorrect.setQuestion(question);

        Option newCorrect = new Option();
        newCorrect.setCorrect(true);

        when(questionRepo.findById(questionId)).thenReturn(Optional.of(question));
        when(optionRepo.findByQuestionId(questionId)).thenReturn(List.of(existingCorrect));
        when(optionRepo.save(newCorrect)).thenReturn(newCorrect);

        service.create(questionId, newCorrect);

        assertFalse(existingCorrect.isCorrect());
        verify(optionRepo).saveAll(List.of(existingCorrect));
        verify(optionRepo).save(newCorrect);
    }

    @Test
    void testCreateOptionQuestionNotFound() {
        UUID id = UUID.randomUUID();
        Option option = new Option();

        when(questionRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(QuestionNotFoundException.class,
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

        List<Option> result = service.findByQuestion(questionId);

        assertEquals(1, result.size());
        verify(optionRepo).findByQuestionId(questionId);
    }

    @Test
    void testUpdateOption() {
        UUID questionId = UUID.randomUUID();
        UUID optionId = UUID.randomUUID();

        Question question = new Question();
        question.setId(questionId);

        Option existing = new Option();
        existing.setId(optionId);
        existing.setQuestion(question);
        existing.setOptionText("Old");
        existing.setCorrect(false);

        Option update = new Option();
        update.setOptionText("New");
        update.setCorrect(true);

        when(optionRepo.findById(optionId)).thenReturn(Optional.of(existing));
        when(optionRepo.findByQuestionId(questionId)).thenReturn(List.of(existing));
        when(optionRepo.save(existing)).thenReturn(existing);

        Option result = service.update(optionId, update);

        assertEquals("New", result.getOptionText());
        assertTrue(result.isCorrect());
        verify(optionRepo).save(existing);
    }

    @Test
    void updateCorrectOption_shouldClearOtherCorrectOptions() {
        UUID questionId = UUID.randomUUID();
        UUID optionId = UUID.randomUUID();

        Question question = new Question();
        question.setId(questionId);

        Option existing = new Option();
        existing.setId(optionId);
        existing.setQuestion(question);
        existing.setCorrect(false);

        Option otherCorrect = new Option();
        otherCorrect.setId(UUID.randomUUID());
        otherCorrect.setQuestion(question);
        otherCorrect.setCorrect(true);

        Option update = new Option();
        update.setOptionText("Updated");
        update.setCorrect(true);

        when(optionRepo.findById(optionId)).thenReturn(Optional.of(existing));
        when(optionRepo.findByQuestionId(questionId)).thenReturn(List.of(existing, otherCorrect));
        when(optionRepo.save(existing)).thenReturn(existing);

        service.update(optionId, update);

        assertTrue(existing.isCorrect());
        assertFalse(otherCorrect.isCorrect());
        verify(optionRepo).saveAll(List.of(existing, otherCorrect));
        verify(optionRepo).save(existing);
    }

    @Test
    void updateOptionNotFound_shouldThrowException() {
        UUID optionId = UUID.randomUUID();
        Option update = new Option();

        when(optionRepo.findById(optionId)).thenReturn(Optional.empty());

        OptionNotFoundException exception = assertThrows(
                OptionNotFoundException.class,
                () -> service.update(optionId, update)
        );

        assertEquals("Option not found: " + optionId, exception.getMessage());
        verify(optionRepo, never()).save(any());
    }

    @Test
    void testDeleteOption() {
        UUID id = UUID.randomUUID();

        service.delete(id);

        verify(optionRepo).deleteById(id);
    }
}
