package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuestionRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionServiceImplTest {

    QuestionRepository questionRepo = mock(QuestionRepository.class);
    ReadingRepository readingRepo = mock(ReadingRepository.class);

    QuestionServiceImpl service = new QuestionServiceImpl(questionRepo, readingRepo);

    @Test
    void testCreateQuestion() {
        UUID readingId = UUID.randomUUID();

        Reading reading = new Reading();
        reading.setId(readingId);

        Question question = new Question();

        when(readingRepo.findById(readingId)).thenReturn(Optional.of(reading));
        when(questionRepo.save(question)).thenReturn(question);

        Question result = service.create(readingId, question);

        assertEquals(reading, result.getReading());
        verify(questionRepo).save(question);
    }

    @Test
    void testCreateQuestionReadingNotFound() {
        UUID readingId = UUID.randomUUID();
        Question question = new Question();

        when(readingRepo.findById(readingId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.create(readingId, question));
    }

    @Test
    void testFindByReading() {
        UUID readingId = UUID.randomUUID();

        Reading reading = new Reading();
        reading.setId(readingId);

        Question q = new Question();
        q.setReading(reading);

        when(questionRepo.findAll()).thenReturn(List.of(q));

        List<Question> result = service.findByReading(readingId);

        assertEquals(1, result.size());
    }

    @Test
    void testDeleteQuestion() {
        UUID id = UUID.randomUUID();

        service.delete(id);

        verify(questionRepo).deleteById(id);
    }
}