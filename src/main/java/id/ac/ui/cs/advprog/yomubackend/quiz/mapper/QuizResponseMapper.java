package id.ac.ui.cs.advprog.yomubackend.quiz.mapper;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.OptionResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuestionResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizOptionResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizQuestionResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.ReadingListItemResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.ReadingResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Option;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuizResponseMapper {

    public ReadingListItemResponse toReadingListItem(Reading reading, boolean completed) {
        return ReadingListItemResponse.builder()
                .id(reading.getId())
                .title(reading.getTitle())
                .category(reading.getCategory())
                .completed(completed)
                .build();
    }

    public ReadingResponse toReadingResponse(Reading reading) {
        return ReadingResponse.builder()
                .id(reading.getId())
                .title(reading.getTitle())
                .category(reading.getCategory())
                .content(reading.getContent())
                .build();
    }

    public QuestionResponse toQuestionResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .build();
    }

    public OptionResponse toOptionResponse(Option option) {
        return OptionResponse.builder()
                .id(option.getId())
                .optionText(option.getOptionText())
                .isCorrect(option.isCorrect())
                .build();
    }

    public QuizResponse toQuizResponse(Reading reading) {
        QuizResponse response = new QuizResponse();
        response.setId(reading.getId());
        response.setTitle(reading.getTitle());
        response.setCategory(reading.getCategory());
        response.setQuestions(toQuestionResponses(reading.getQuestions()));
        return response;
    }

    private List<QuizQuestionResponse> toQuestionResponses(List<Question> questions) {
        if (questions == null) {
            return List.of();
        }

        return questions.stream()
                .map(this::toQuizQuestionResponse)
                .toList();
    }

    private QuizQuestionResponse toQuizQuestionResponse(Question question) {
        return QuizQuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .options(toOptionResponses(question.getOptions()))
                .build();
    }

    private List<QuizOptionResponse> toOptionResponses(List<Option> options) {
        if (options == null) {
            return List.of();
        }

        return options.stream()
                .map(option -> QuizOptionResponse.builder()
                        .id(option.getId())
                        .optionText(option.getOptionText())
                        .build())
                .toList();
    }
}
