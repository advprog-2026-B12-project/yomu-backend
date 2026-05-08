package id.ac.ui.cs.advprog.yomubackend.clan.league;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DiamondScoreProvider implements ClanScoreProvider {

    private static final double SCORE_WEIGHT = 0.5;
    private static final double ACTIVITY_WEIGHT = 0.2;
    private static final double ACCURACY_WEIGHT = 0.3;
    private static final int QUIZ_ACTIVITY_SCALE = 10;
    private static final int ACCURACY_SCALE = 100;

    @Override
    public String getDivision() {
        return "DIAMOND";
    }

    @Override
    public double calculateScore(List<MemberStat> stats) {
        if (stats.isEmpty()) {
            return 0.0;
        }

        double averageScore = stats.stream()
                .mapToDouble(MemberStat::totalScore)
                .average()
                .orElse(0.0);
        double averageActivity = stats.stream()
                .mapToDouble(MemberStat::quizCount)
                .average()
                .orElse(0.0);
        double averageAccuracy = stats.stream()
                .mapToDouble(MemberStat::accuracy)
                .average()
                .orElse(0.0);

        return (averageScore * SCORE_WEIGHT)
                + (averageActivity * QUIZ_ACTIVITY_SCALE * ACTIVITY_WEIGHT)
                + (averageAccuracy * ACCURACY_SCALE * ACCURACY_WEIGHT);
    }
}
