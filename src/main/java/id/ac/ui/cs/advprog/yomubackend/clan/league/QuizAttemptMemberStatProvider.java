package id.ac.ui.cs.advprog.yomubackend.clan.league;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Primary
public class QuizAttemptMemberStatProvider implements MemberStatProvider {

    private final QuizAttemptRepository quizAttemptRepository;

    public QuizAttemptMemberStatProvider(QuizAttemptRepository quizAttemptRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
    }

    @Override
    public MemberStat getStatForUser(UUID userId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserId(userId);

        int totalScore = attempts.stream()
                .mapToInt(QuizAttempt::getScore)
                .sum();
        int totalQuestions = attempts.stream()
                .mapToInt(QuizAttempt::getTotal)
                .sum();
        int quizCount = attempts.size();
        double accuracy = totalQuestions == 0 ? 0.0 : (double) totalScore / totalQuestions;

        return new MemberStat(userId, totalScore, quizCount, accuracy);
    }
}
