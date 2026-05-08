package id.ac.ui.cs.advprog.yomubackend.clan.league;

public record MemberStat(
        Long userId,
        int totalScore,
        int quizCount,
        double accuracy
) {
}