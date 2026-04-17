package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ClanMemberResponseTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        Instant joinedAt = Instant.parse("2026-01-01T00:00:00Z");

        ClanMemberResponse response = new ClanMemberResponse(
                10L,
                ClanMember.Role.MEMBER,
                joinedAt
        );

        assertEquals(10L, response.getUserId());
        assertEquals(ClanMember.Role.MEMBER, response.getRole());
        assertEquals(joinedAt, response.getJoinedAt());
    }

    @Test
    void testSetters() {
        Instant joinedAt = Instant.parse("2026-01-01T00:00:00Z");
        Instant newJoinedAt = Instant.parse("2026-02-01T00:00:00Z");

        ClanMemberResponse response = new ClanMemberResponse(
                1L,
                ClanMember.Role.MEMBER,
                joinedAt
        );

        response.setUserId(2L);
        response.setRole(ClanMember.Role.LEADER);
        response.setJoinedAt(newJoinedAt);

        assertEquals(2L, response.getUserId());
        assertEquals(ClanMember.Role.LEADER, response.getRole());
        assertEquals(newJoinedAt, response.getJoinedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        Instant joinedAt = Instant.parse("2026-01-01T00:00:00Z");

        ClanMemberResponse response1 = new ClanMemberResponse(
                5L,
                ClanMember.Role.MEMBER,
                joinedAt
        );

        ClanMemberResponse response2 = new ClanMemberResponse(
                5L,
                ClanMember.Role.MEMBER,
                joinedAt
        );

        ClanMemberResponse response3 = new ClanMemberResponse(
                6L,
                ClanMember.Role.LEADER,
                joinedAt
        );

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void testToString() {
        Instant joinedAt = Instant.parse("2026-01-01T00:00:00Z");

        ClanMemberResponse response = new ClanMemberResponse(
                7L,
                ClanMember.Role.MEMBER,
                joinedAt
        );

        String result = response.toString();

        assertNotNull(result);
        assertTrue(result.contains("7"));
        assertTrue(result.contains("MEMBER"));
        assertTrue(result.contains("2026-01-01T00:00:00Z"));
    }
}