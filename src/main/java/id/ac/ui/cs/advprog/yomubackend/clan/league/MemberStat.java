package id.ac.ui.cs.advprog.yomubackend.clan.league;

import java.util.UUID;

public record MemberStat(
        UUID userId,
        int totalScore,
        int quizCount,
        double accuracy
) {
}