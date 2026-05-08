package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClanResponseTest {

    private static final UUID LEADER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID OTHER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Test
    void testAllArgsConstructorAndGetters() {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

        ClanResponse response = new ClanResponse(
                1L,
                "Warriors",
                "Best clan",
                LEADER_ID,
                "GOLD",
                25L,
                createdAt
        );

        assertEquals(1L, response.getId());
        assertEquals("Warriors", response.getName());
        assertEquals("Best clan", response.getDescription());
        assertEquals(LEADER_ID, response.getLeaderUserId());
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
                LEADER_ID,
                "GOLD",
                25L,
                createdAt
        );

        response.setId(2L);
        response.setName("Rangers");
        response.setDescription("New description");
        response.setLeaderUserId(OTHER_ID);
        response.setDivision("SILVER");
        response.setMemberCount(15L);
        response.setCreatedAt(newCreatedAt);

        assertEquals(2L, response.getId());
        assertEquals("Rangers", response.getName());
        assertEquals("New description", response.getDescription());
        assertEquals(OTHER_ID, response.getLeaderUserId());
        assertEquals("SILVER", response.getDivision());
        assertEquals(15L, response.getMemberCount());
        assertEquals(newCreatedAt, response.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

        ClanResponse response1 = new ClanResponse(
                1L, "Warriors", "Best clan", LEADER_ID, "GOLD", 25L, createdAt
        );
        ClanResponse response2 = new ClanResponse(
                1L, "Warriors", "Best clan", LEADER_ID, "GOLD", 25L, createdAt
        );
        ClanResponse response3 = new ClanResponse(
                2L, "Rangers", "Other clan", OTHER_ID, "SILVER", 10L, createdAt
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
                LEADER_ID,
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
