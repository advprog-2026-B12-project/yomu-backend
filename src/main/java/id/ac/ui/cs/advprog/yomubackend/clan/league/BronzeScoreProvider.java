package id.ac.ui.cs.advprog.yomubackend.clan.league;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BronzeScoreProvider implements ClanScoreProvider {

    @Override
    public String getDivision() {
        return "BRONZE";
    }

    @Override
    public double calculateScore(List<MemberStat> stats) {
        return stats.stream()
                .mapToInt(MemberStat::totalScore)
                .sum();
    }
}