package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ClanResponseTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

        ClanResponse response = new ClanResponse(
                1L,
                "Warriors",
                "Best clan",
                100L,
                "GOLD",
                25L,
                createdAt
        );

        assertEquals(1L, response.getId());
        assertEquals("Warriors", response.getName());
        assertEquals("Best clan", response.getDescription());
        assertEquals(100L, response.getLeaderUserId());
        assertEquals("GOLD", response.getDivision());
        assertEquals(25L, response.getMemberCount());
        assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    void testSetters() {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        Instant newCreatedAt = Instant.parse("2026-02-01T00:00:00Z");

        ClanResponse response = new ClanResponse(
                1L,
                "Warriors",
                "Best clan",
                100L,
                "GOLD",
                25L,
                createdAt
        );

        response.setId(2L);
        response.setName("Rangers");
        response.setDescription("New description");
        response.setLeaderUserId(200L);
        response.setDivision("SILVER");
        response.setMemberCount(15L);
        response.setCreatedAt(newCreatedAt);

        assertEquals(2L, response.getId());
        assertEquals("Rangers", response.getName());
        assertEquals("New description", response.getDescription());
        assertEquals(200L, response.getLeaderUserId());
        assertEquals("SILVER", response.getDivision());
        assertEquals(15L, response.getMemberCount());
        assertEquals(newCreatedAt, response.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

        ClanResponse response1 = new ClanResponse(
                1L, "Warriors", "Best clan", 100L, "GOLD", 25L, createdAt
        );

        ClanResponse response2 = new ClanResponse(
                1L, "Warriors", "Best clan", 100L, "GOLD", 25L, createdAt
        );

        ClanResponse response3 = new ClanResponse(
                2L, "Rangers", "Other clan", 101L, "SILVER", 10L, createdAt
        );

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testToString() {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

        ClanResponse response = new ClanResponse(
                1L,
                "Warriors",
                "Best clan",
                100L,
                "GOLD",
                25L,
                createdAt
        );

        String result = response.toString();

        assertNotNull(result);
        assertTrue(result.contains("Warriors"));
        assertTrue(result.contains("Best clan"));
        assertTrue(result.contains("GOLD"));
    }
}