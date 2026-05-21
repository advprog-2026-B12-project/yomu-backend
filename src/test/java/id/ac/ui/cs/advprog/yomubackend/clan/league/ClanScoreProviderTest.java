package id.ac.ui.cs.advprog.yomubackend.clan.league;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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

    // ── getDivision() ─────────────────────────────────────────────────────────

    @Test
    void bronzeScoreProvider_getDivision_returnsBronze() {
        assertEquals("BRONZE", new BronzeScoreProvider().getDivision());
    }

    @Test
    void silverScoreProvider_getDivision_returnsSilver() {
        assertEquals("SILVER", new SilverScoreProvider().getDivision());
    }

    @Test
    void goldScoreProvider_getDivision_returnsGold() {
        assertEquals("GOLD", new GoldScoreProvider().getDivision());
    }

    @Test
    void diamondScoreProvider_getDivision_returnsDiamond() {
        assertEquals("DIAMOND", new DiamondScoreProvider().getDivision());
    }

    // ── Empty-list edge cases ─────────────────────────────────────────────────

    @Test
    void bronzeScoreProvider_emptyList_returnsZero() {
        assertEquals(0.0, new BronzeScoreProvider().calculateScore(List.of()));
    }

    @Test
    void silverScoreProvider_emptyList_returnsZero() {
        assertEquals(0.0, new SilverScoreProvider().calculateScore(List.of()));
    }

    @Test
    void goldScoreProvider_emptyList_returnsZero() {
        assertEquals(0.0, new GoldScoreProvider().calculateScore(List.of()));
    }

    // ── Single-member edge cases ──────────────────────────────────────────────

    @Test
    void bronzeScoreProvider_singleMember_returnsMemberScore() {
        assertEquals(200.0, new BronzeScoreProvider().calculateScore(List.of(stat(USER_A, 200, 5, 0.8))));
    }

    @Test
    void silverScoreProvider_singleMember_includesActivityBonus() {
        // 300 + (4 * 10) = 340
        assertEquals(340.0, new SilverScoreProvider().calculateScore(List.of(stat(USER_A, 300, 4, 0.7))));
    }

    @Test
    void goldScoreProvider_singleMember_returnsScoreTimesAccuracy() {
        assertEquals(80.0, new GoldScoreProvider().calculateScore(List.of(stat(USER_A, 100, 3, 0.8))), 1e-9);
    }

    @Test
    void diamondScoreProvider_singleMember_returnsWeightedScore() {
        // avg score=100, avg activity=4, avg accuracy=0.8
        // (100*0.5) + (4*10*0.2) + (0.8*100*0.3) = 50 + 8 + 24 = 82
        assertEquals(82.0, new DiamondScoreProvider().calculateScore(List.of(stat(USER_A, 100, 4, 0.8))), 1e-9);
    }

    private MemberStat stat(UUID userId, int totalScore, int quizCount, double accuracy) {
        return new MemberStat(userId, totalScore, quizCount, accuracy);
    }

    // ── ClanScoreProviderResolver ─────────────────────────────────────────────

    private final ClanScoreProviderResolver resolver = new ClanScoreProviderResolver(List.of(
            new BronzeScoreProvider(),
            new SilverScoreProvider(),
            new GoldScoreProvider(),
            new DiamondScoreProvider()
    ));

    @Test
    void resolver_bronze_returnsBronzeProvider() {
        assertInstanceOf(BronzeScoreProvider.class, resolver.resolve("BRONZE"));
    }

    @Test
    void resolver_silver_returnsSilverProvider() {
        assertInstanceOf(SilverScoreProvider.class, resolver.resolve("SILVER"));
    }

    @Test
    void resolver_gold_returnsGoldProvider() {
        assertInstanceOf(GoldScoreProvider.class, resolver.resolve("GOLD"));
    }

    @Test
    void resolver_diamond_returnsDiamondProvider() {
        assertInstanceOf(DiamondScoreProvider.class, resolver.resolve("DIAMOND"));
    }

    @Test
    void resolver_lowercaseInput_resolvedCaseInsensitively() {
        assertInstanceOf(BronzeScoreProvider.class, resolver.resolve("bronze"));
    }

    @Test
    void resolver_mixedCaseInput_resolvedCaseInsensitively() {
        assertInstanceOf(GoldScoreProvider.class, resolver.resolve("Gold"));
    }

    @Test
    void resolver_unknownDivision_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve("PLATINUM")
        );
        assertTrue(ex.getMessage().contains("PLATINUM"));
    }

    @Test
    void resolver_emptyString_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> resolver.resolve(""));
    }
}
