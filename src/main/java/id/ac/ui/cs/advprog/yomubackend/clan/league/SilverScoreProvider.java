package id.ac.ui.cs.advprog.yomubackend.clan.league;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SilverScoreProvider implements ClanScoreProvider {

    private static final int QUIZ_ACTIVITY_BONUS = 10;

    @Override
    public String getDivision() {
        return "SILVER";
    }

    @Override
    public double calculateScore(List<MemberStat> stats) {
        return stats.stream()
                .mapToInt(stat -> stat.totalScore() + (stat.quizCount() * QUIZ_ACTIVITY_BONUS))
                .sum();
    }
}
