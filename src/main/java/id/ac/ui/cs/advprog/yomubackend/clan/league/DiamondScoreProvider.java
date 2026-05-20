package id.ac.ui.cs.advprog.yomubackend.clan.league;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DiamondScoreProvider implements ClanScoreProvider {

    // True weighted average: both components scaled to 0-100 range.
    // Accuracy [0,1] × 100 → [0,100]. Activity × ACTIVITY_SCALE caps ~100 for ~10 quizzes.
    // Weights sum to 1.0 — accuracy dominates because Diamond rewards quality over quantity.
    private static final double TOTAL_SCORE_WEIGHT = 0.5;
    private static final double ACCURACY_WEIGHT = 0.3;
    private static final double ACTIVITY_WEIGHT = 0.2;
    private static final int ACCURACY_SCALE = 100;
    private static final int ACTIVITY_SCALE = 10;

    @Override
    public String getDivision() {
        return LeagueDivision.DIAMOND.value();
    }

    @Override
    public double calculateScore(List<MemberStat> stats) {
        if (stats.isEmpty()) {
            return 0.0;
        }

        double averageTotalScore = stats.stream()
                .mapToDouble(MemberStat::totalScore)
                .average()
                .orElse(0.0);
        double averageAccuracy = stats.stream()
                .mapToDouble(MemberStat::accuracy)
                .average()
                .orElse(0.0);
        double averageActivity = stats.stream()
                .mapToDouble(MemberStat::quizCount)
                .average()
                .orElse(0.0);

        return (averageTotalScore * TOTAL_SCORE_WEIGHT)
                + (averageAccuracy * ACCURACY_SCALE * ACCURACY_WEIGHT)
                + (averageActivity * ACTIVITY_SCALE * ACTIVITY_WEIGHT);
    }
}
