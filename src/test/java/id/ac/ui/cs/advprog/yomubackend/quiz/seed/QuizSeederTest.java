package id.ac.ui.cs.advprog.yomubackend.quiz.seed;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class QuizSeederTest {

    ReadingRepository repository = mock(ReadingRepository.class);
    QuizSeeder seeder = new QuizSeeder(repository);

    @Test
    void testSeed_WhenDataExists_ShouldDoNothing() {
        when(repository.count()).thenReturn(1L);

        seeder.seed();

        verify(repository, never()).save(any());
    }

    @Test
    void testSeed_WhenEmpty_ShouldInsertData() {
        when(repository.count()).thenReturn(0L);

        seeder.seed();

        verify(repository).save(any(Reading.class));
    }

    @Test
    void testSeed_DataContent() {
        when(repository.count()).thenReturn(0L);

        seeder.seed();

        var captor = org.mockito.ArgumentCaptor.forClass(Reading.class);
        verify(repository).save(captor.capture());

        Reading saved = captor.getValue();

        assertEquals("The Importance of Sleep", saved.getTitle());
        assertEquals(2, saved.getQuestions().size());
    }
}