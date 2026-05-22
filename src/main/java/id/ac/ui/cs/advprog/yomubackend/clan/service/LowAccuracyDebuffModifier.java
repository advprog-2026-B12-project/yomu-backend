package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class LowAccuracyDebuffModifier implements ClanScoreModifier {

    private static final double LOW_ACCURACY_THRESHOLD = 0.5;
    private static final double ACCURACY_DEBUFF_MULTIPLIER = 0.8;
    private static final double NEUTRAL_MULTIPLIER = 1.0;
    private static final int ACCURACY_WINDOW_DAYS = 7;

    private final QuizAttemptRepository quizAttemptRepository;
    private final Clock clock;

    @Autowired
    public LowAccuracyDebuffModifier(QuizAttemptRepository quizAttemptRepository) {
        this(quizAttemptRepository, Clock.systemDefaultZone());
    }

    LowAccuracyDebuffModifier(QuizAttemptRepository quizAttemptRepository,
                              Clock clock) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.clock = clock;
    }

    @Override
    public String getModifierName() {
        return "Low Accuracy Penalty";
    }

    @Override
    public double calculateMultiplier(List<ClanMember> members) {
        if (members == null || members.isEmpty()) {
            return NEUTRAL_MULTIPLIER;
        }

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime currentStart = now.minusDays(ACCURACY_WINDOW_DAYS);

        AccuracySummary current = summarizeAccuracy(members, currentStart, now);
        if (current.totalQuestions() == 0) {
            return NEUTRAL_MULTIPLIER;
        }

        if (current.accuracy() < LOW_ACCURACY_THRESHOLD) {
            return ACCURACY_DEBUFF_MULTIPLIER;
        }

        LocalDateTime previousStart = now.minusDays(ACCURACY_WINDOW_DAYS * 2L);
        AccuracySummary previous = summarizeAccuracy(members, previousStart, currentStart);

        return previous.totalQuestions() > 0 && current.accuracy() < previous.accuracy()
                ? ACCURACY_DEBUFF_MULTIPLIER
                : NEUTRAL_MULTIPLIER;
    }

    private AccuracySummary summarizeAccuracy(List<ClanMember> members,
                                              LocalDateTime start,
                                              LocalDateTime end) {
        List<UUID> memberIds = members.stream().map(ClanMember::getUserId).toList();
        List<QuizAttempt> attempts = quizAttemptRepository
                .findByUserIdInAndCreatedAtBetween(memberIds, start, end);

        int score = attempts.stream().mapToInt(QuizAttempt::getScore).sum();
        int total = attempts.stream().mapToInt(QuizAttempt::getTotal).sum();
        return new AccuracySummary(score, total);
    }

    private record AccuracySummary(int score, int totalQuestions) {
        double accuracy() {
            return totalQuestions == 0 ? 0.0 : (double) score / totalQuestions;
        }
    }
}
