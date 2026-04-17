package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizSubmitRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Option;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class QuizServiceImpl implements QuizService {

    private final ReadingService readingService;
    private final QuizAttemptRepository quizAttemptRepository;

    public QuizServiceImpl(ReadingService readingService,
                           QuizAttemptRepository quizAttemptRepository) {
        this.readingService = readingService;
        this.quizAttemptRepository = quizAttemptRepository;
    }

    @Override
    public QuizResultResponse submit(QuizSubmitRequest request) {
        Reading reading = readingService.findById(request.getReadingId());

        int correct = 0;

        for (Question q : reading.getQuestions()) {
            String selected = request.getAnswers().get(q.getId().toString());

            Option correctOption = q.getOptions().stream()
                    .filter(Option::isCorrect)
                    .findFirst()
                    .orElse(null);

            if (correctOption != null &&
                    correctOption.getId().toString().equals(selected)) {
                correct++;
            }
        }

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUserId(request.getUserId()); // can mock for now
        attempt.setReadingId(reading.getId());
        attempt.setScore(correct);
        attempt.setTotal(reading.getQuestions().size());
        attempt.setCreatedAt(LocalDateTime.now());

        quizAttemptRepository.save(attempt);

        return new QuizResultResponse(correct, reading.getQuestions().size());
    }
}