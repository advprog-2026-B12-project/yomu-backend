package id.ac.ui.cs.advprog.yomubackend.quiz.service;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizSubmitRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.completion.QuizCompletion;
import id.ac.ui.cs.advprog.yomubackend.quiz.completion.QuizCompletionProcessor;
import id.ac.ui.cs.advprog.yomubackend.quiz.exception.QuizAlreadyCompletedException;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Option;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class QuizServiceImpl implements QuizService {

    private final ReadingService readingService;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizCompletionProcessor quizCompletionProcessor;
    private final ReadingProgressService readingProgressService;

    public QuizServiceImpl(ReadingService readingService,
            QuizAttemptRepository quizAttemptRepository,
            QuizCompletionProcessor quizCompletionProcessor,
            ReadingProgressService readingProgressService) {
        this.readingService = readingService;
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizCompletionProcessor = quizCompletionProcessor;
        this.readingProgressService = readingProgressService;
    }

    @Override
    @Transactional
    public QuizResultResponse submit(UUID userId, QuizSubmitRequest request) {
        validateSubmitRequest(userId, request);
        ensureNotCompleted(userId, request.getReadingId());
        readingProgressService.ensureOpened(userId, request.getReadingId());

        Reading reading = readingService.findById(request.getReadingId());
        Map<String, String> answers = request.getAnswers() == null ? Map.of() : request.getAnswers();
        List<Question> questions = reading.getQuestions() == null ? List.of() : reading.getQuestions();
        int correctAnswers = countCorrectAnswers(questions, answers);
        int totalQuestions = questions.size();
        LocalDateTime completedAt = LocalDateTime.now();

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUserId(userId);
        attempt.setReadingId(reading.getId());
        attempt.setScore(correctAnswers);
        attempt.setTotal(totalQuestions);
        attempt.setCreatedAt(completedAt);

        quizAttemptRepository.save(attempt);
        quizCompletionProcessor.processCompletion(new QuizCompletion(
                userId,
                reading.getId(),
                correctAnswers,
                totalQuestions,
                completedAt));

        return new QuizResultResponse(correctAnswers, totalQuestions);
    }

    @Override
    public boolean hasCompleted(UUID userId, UUID readingId) {
        return quizAttemptRepository.existsByUserIdAndReadingId(userId, readingId);
    }

    @Override
    public void ensureNotCompleted(UUID userId, UUID readingId) {
        if (hasCompleted(userId, readingId)) {
            throw new QuizAlreadyCompletedException();
        }
    }

    private void validateSubmitRequest(UUID userId, QuizSubmitRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (request.getReadingId() == null) {
            throw new IllegalArgumentException("Reading ID is required");
        }
    }

    private int countCorrectAnswers(List<Question> questions, Map<String, String> answers) {
        int correctAnswers = 0;

        for (Question question : questions) {
            String selectedOptionId = answers.get(question.getId().toString());

            List<Option> options = question.getOptions() == null ? List.of() : question.getOptions();
            Option correctOption = options.stream()
                    .filter(Option::isCorrect)
                    .findFirst()
                    .orElse(null);

            if (correctOption != null && correctOption.getId().toString().equals(selectedOptionId)) {
                correctAnswers++;
            }
        }

        return correctAnswers;
    }
}
