package id.ac.ui.cs.advprog.yomubackend.quiz.seed;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.*;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("!test")
@Component
public class QuizSeeder {

    private final ReadingRepository readingRepository;

    public QuizSeeder(ReadingRepository readingRepository) {
        this.readingRepository = readingRepository;
    }

    @PostConstruct
    public void seed() {
        if (readingRepository.count() > 0) return;

        Reading reading = new Reading();
        reading.setTitle("The Importance of Sleep");
        reading.setCategory("Health");
        reading.setContent("Sleep improves memory, mood, and brain function.");

        Question q1 = new Question();
        q1.setQuestionText("What does sleep improve?");
        q1.setReading(reading);

        Option o1 = new Option();
        o1.setOptionText("Memory");
        o1.setCorrect(true);
        o1.setQuestion(q1);

        Option o2 = new Option();
        o2.setOptionText("Stress");
        o2.setCorrect(false);
        o2.setQuestion(q1);

        q1.setOptions(List.of(o1, o2));

        Question q2 = new Question();
        q2.setQuestionText("Sleep affects what?");
        q2.setReading(reading);

        Option o3 = new Option();
        o3.setOptionText("Mood");
        o3.setCorrect(true);
        o3.setQuestion(q2);

        Option o4 = new Option();
        o4.setOptionText("Nothing");
        o4.setCorrect(false);
        o4.setQuestion(q2);

        q2.setOptions(List.of(o3, o4));

        reading.setQuestions(List.of(q1, q2));

        readingRepository.save(reading);
    }
}
