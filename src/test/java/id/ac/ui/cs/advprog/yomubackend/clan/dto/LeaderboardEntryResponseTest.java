package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeaderboardEntryResponseTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        LeaderboardEntryResponse response = new LeaderboardEntryResponse(
                1,
                10L,
                "Warriors",
                "GOLD",
                25L,
                999
        );

        assertEquals(1, response.getRank());
        assertEquals(10L, response.getClanId());
        assertEquals("Warriors", response.getClanName());
        assertEquals("GOLD", response.getDivision());
        assertEquals(25L, response.getMemberCount());
        assertEquals(999, response.getScore());
    }

    @Test
    void testSetters() {
        LeaderboardEntryResponse response = new LeaderboardEntryResponse(
                1,
                10L,
                "Warriors",
                "GOLD",
                25L,
                999
        );

        response.setRank(2);
        response.setClanId(20L);
        response.setClanName("Rangers");
        response.setDivision("SILVER");
        response.setMemberCount(15L);
        response.setScore(500);

        assertEquals(2, response.getRank());
        assertEquals(20L, response.getClanId());
        assertEquals("Rangers", response.getClanName());
        assertEquals("SILVER", response.getDivision());
        assertEquals(15L, response.getMemberCount());
        assertEquals(500, response.getScore());
    }

    @Test
    void testEqualsAndHashCode() {
        LeaderboardEntryResponse response1 = new LeaderboardEntryResponse(
                1, 10L, "Warriors", "GOLD", 25L, 999
        );

        LeaderboardEntryResponse response2 = new LeaderboardEntryResponse(
                1, 10L, "Warriors", "GOLD", 25L, 999
        );

        LeaderboardEntryResponse response3 = new LeaderboardEntryResponse(
                2, 20L, "Rangers", "SILVER", 15L, 500
        );

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testToString() {
        LeaderboardEntryResponse response = new LeaderboardEntryResponse(
                1, 10L, "Warriors", "GOLD", 25L, 999
        );

        String result = response.toString();

        assertNotNull(result);
        assertTrue(result.contains("Warriors"));
        assertTrue(result.contains("GOLD"));
        assertTrue(result.contains("999"));
    }
}