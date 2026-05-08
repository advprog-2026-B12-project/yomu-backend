package id.ac.ui.cs.advprog.yomubackend.clan.league;

import java.util.List;

public interface ClanScoreProvider {
    String getDivision();
    double calculateScore(List<MemberStat> stats);
}