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

        verify(repository, never()).saveAll(any());
    }

    @Test
    void testSeed_WhenEmpty_ShouldInsertData() {
        when(repository.count()).thenReturn(0L);

        seeder.seed();

        verify(repository).saveAll(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSeed_DataContent() {
        when(repository.count()).thenReturn(0L);

        seeder.seed();

        var captor = org.mockito.ArgumentCaptor.forClass(Iterable.class);
        verify(repository).saveAll(captor.capture());

        var saved = (java.util.List<Reading>) captor.getValue();
        assertEquals(4, saved.size());

        var titles = saved.stream().map(Reading::getTitle).toList();
        assertTrue(titles.contains("Maraknya Hoaks di Era Digital"));
        assertTrue(titles.contains("Timnas Indonesia Melaju ke Babak Final"));
        assertTrue(titles.contains("Kecerdasan Buatan dalam Kehidupan Sehari-hari"));
        assertTrue(titles.contains("Pentingnya Literasi Informasi di Era Modern"));

        var categories = saved.stream().map(Reading::getCategory).distinct().sorted().toList();
        assertEquals(java.util.List.of("BERITA", "BUDAYA", "OLAHRAGA", "TEKNOLOGI"), categories);
    }
}