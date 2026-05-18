package id.ac.ui.cs.advprog.yomubackend.quiz.mapper;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.ReadingListItemResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.ReadingResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Option;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizResponseMapperTest {

    private final QuizResponseMapper mapper = new QuizResponseMapper();

    @Test
    void toQuizResponse_hidesReadingContentAndCorrectFlag() {
        Reading reading = buildReading();

        QuizResponse response = mapper.toQuizResponse(reading);

        assertEquals(reading.getId(), response.getId());
        assertEquals(reading.getTitle(), response.getTitle());
        assertEquals(reading.getCategory(), response.getCategory());
        assertEquals(1, response.getQuestions().size());
        assertEquals(2, response.getQuestions().getFirst().getOptions().size());
        assertEquals("Correct", response.getQuestions().getFirst().getOptions().getFirst().getOptionText());
    }

    @Test
    void toReadingResponse_includesContentForReadingPhase() {
        Reading reading = buildReading();

        ReadingResponse response = mapper.toReadingResponse(reading);

        assertEquals(reading.getId(), response.getId());
        assertEquals(reading.getTitle(), response.getTitle());
        assertEquals(reading.getCategory(), response.getCategory());
        assertEquals(reading.getContent(), response.getContent());
    }

    @Test
    void toReadingListItem_includesCompletionStatusOnly() {
        Reading reading = buildReading();

        ReadingListItemResponse response = mapper.toReadingListItem(reading, true);

        assertEquals(reading.getId(), response.getId());
        assertEquals(reading.getTitle(), response.getTitle());
        assertEquals(reading.getCategory(), response.getCategory());
        assertEquals(true, response.isCompleted());
    }

    private Reading buildReading() {
        Option correct = new Option();
        correct.setId(UUID.randomUUID());
        correct.setOptionText("Correct");
        correct.setCorrect(true);

        Option wrong = new Option();
        wrong.setId(UUID.randomUUID());
        wrong.setOptionText("Wrong");
        wrong.setCorrect(false);

        Question question = new Question();
        question.setId(UUID.randomUUID());
        question.setQuestionText("Question?");
        question.setOptions(List.of(correct, wrong));

        Reading reading = new Reading();
        reading.setId(UUID.randomUUID());
        reading.setTitle("Reading");
        reading.setCategory("News & Media");
        reading.setContent("Content");
        reading.setQuestions(List.of(question));
        return reading;
    }
}
