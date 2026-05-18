package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserDailyMissionRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClanScoreModifierService {

    private static final double DAILY_MISSION_COMPLETION_THRESHOLD = 0.7;
    private static final double DAILY_MISSION_BUFF_MULTIPLIER = 1.2;
    private static final double ACCURACY_DEBUFF_MULTIPLIER = 0.9;
    private static final int ACCURACY_WINDOW_DAYS = 7;

    private final UserDailyMissionRepository userDailyMissionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final Clock clock;

    @Autowired
    public ClanScoreModifierService(UserDailyMissionRepository userDailyMissionRepository,
                                    QuizAttemptRepository quizAttemptRepository) {
        this(userDailyMissionRepository, quizAttemptRepository, Clock.systemDefaultZone());
    }

    ClanScoreModifierService(UserDailyMissionRepository userDailyMissionRepository,
                             QuizAttemptRepository quizAttemptRepository,
                             Clock clock) {
        this.userDailyMissionRepository = userDailyMissionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.clock = clock;
    }

    public double calculateMultiplier(List<ClanMember> members) {
        if (members == null || members.isEmpty()) {
            return 1.0;
        }

        double multiplier = 1.0;

        if (isDailyMissionBuffActive(members)) {
            multiplier *= DAILY_MISSION_BUFF_MULTIPLIER;
        }

        if (isAccuracyDebuffActive(members)) {
            multiplier *= ACCURACY_DEBUFF_MULTIPLIER;
        }

        return multiplier;
    }

    private boolean isDailyMissionBuffActive(List<ClanMember> members) {
        LocalDate today = LocalDate.now(clock);

        long completedMembers = members.stream()
                .filter(member -> hasCompletedDailyMission(member, today))
                .count();

        double completionRate = (double) completedMembers / members.size();
        return completionRate >= DAILY_MISSION_COMPLETION_THRESHOLD;
    }

    private boolean hasCompletedDailyMission(ClanMember member, LocalDate date) {
        return userDailyMissionRepository
                .findByUserIdAndDateAssigned(member.getUserId(), date)
                .stream()
                .anyMatch(mission -> Boolean.TRUE.equals(mission.getIsCompleted()));
    }

    private boolean isAccuracyDebuffActive(List<ClanMember> members) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime currentStart = now.minusDays(ACCURACY_WINDOW_DAYS);
        LocalDateTime previousStart = now.minusDays(ACCURACY_WINDOW_DAYS * 2L);

        AccuracySummary previous = summarizeAccuracy(members, previousStart, currentStart);
        AccuracySummary current = summarizeAccuracy(members, currentStart, now);

        if (previous.totalQuestions() == 0 || current.totalQuestions() == 0) {
            return false;
        }

        return current.accuracy() < previous.accuracy();
    }

    private AccuracySummary summarizeAccuracy(List<ClanMember> members,
                                              LocalDateTime start,
                                              LocalDateTime end) {
        int score = 0;
        int total = 0;

        for (ClanMember member : members) {
            List<QuizAttempt> attempts = quizAttemptRepository
                    .findByUserIdAndCreatedAtBetween(member.getUserId(), start, end);

            score += attempts.stream()
                    .mapToInt(QuizAttempt::getScore)
                    .sum();
            total += attempts.stream()
                    .mapToInt(QuizAttempt::getTotal)
                    .sum();
        }

        return new AccuracySummary(score, total);
    }

    private record AccuracySummary(int score, int totalQuestions) {
        double accuracy() {
            return totalQuestions == 0 ? 0.0 : (double) score / totalQuestions;
        }
    }
}
