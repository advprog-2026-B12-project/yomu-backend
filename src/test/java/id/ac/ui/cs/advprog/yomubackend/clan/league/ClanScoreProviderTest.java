package id.ac.ui.cs.advprog.yomubackend.clan.league;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClanScoreProviderTest {

    private static final UUID USER_A = new UUID(0L, 1L);
    private static final UUID USER_B = new UUID(0L, 2L);

    @Test
    void bronzeScoreProvider_shouldUseTotalScoreOnly() {
        BronzeScoreProvider provider = new BronzeScoreProvider();

        double result = provider.calculateScore(List.of(
                stat(USER_A, 100, 4, 0.8),
                stat(USER_B, 50, 10, 0.4)
        ));

        assertEquals(150.0, result);
    }

    @Test
    void silverScoreProvider_shouldIncludeQuizActivityBonus() {
        SilverScoreProvider provider = new SilverScoreProvider();

        double result = provider.calculateScore(List.of(
                stat(USER_A, 100, 4, 0.8),
                stat(USER_B, 50, 10, 0.4)
        ));

        assertEquals(290.0, result);
    }

    @Test
    void goldScoreProvider_shouldWeightScoreByAccuracy() {
        GoldScoreProvider provider = new GoldScoreProvider();

        double result = provider.calculateScore(List.of(
                stat(USER_A, 100, 4, 0.8),
                stat(USER_B, 50, 10, 0.4)
        ));

        assertEquals(100.0, result);
    }

    @Test
    void diamondScoreProvider_shouldUseWeightedAverage() {
        DiamondScoreProvider provider = new DiamondScoreProvider();

        double result = provider.calculateScore(List.of(
                stat(USER_A, 100, 4, 0.8),
                stat(USER_B, 50, 10, 0.4)
        ));

        assertEquals(69.5, result);
    }

    @Test
    void diamondScoreProvider_shouldReturnZero_whenStatsEmpty() {
        DiamondScoreProvider provider = new DiamondScoreProvider();

        double result = provider.calculateScore(List.of());

        assertEquals(0.0, result);
    }

    private MemberStat stat(UUID userId, int totalScore, int quizCount, double accuracy) {
        return new MemberStat(userId, totalScore, quizCount, accuracy);
    }
}
