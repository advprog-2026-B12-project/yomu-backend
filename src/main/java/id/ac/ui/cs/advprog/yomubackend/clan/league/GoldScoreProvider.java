package id.ac.ui.cs.advprog.yomubackend.clan.league;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoldScoreProvider implements ClanScoreProvider {

    @Override
    public String getDivision() {
        return "GOLD";
    }

    @Override
    public double calculateScore(List<MemberStat> stats) {
        return stats.stream()
                .mapToDouble(stat -> stat.totalScore() * stat.accuracy())
                .sum();
    }
}
