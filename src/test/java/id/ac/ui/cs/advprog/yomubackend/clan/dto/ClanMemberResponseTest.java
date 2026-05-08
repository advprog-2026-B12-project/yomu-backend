package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClanMemberResponseTest {

    private static final UUID MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID OTHER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Test
    void testAllArgsConstructorAndGetters() {
        Instant joinedAt = Instant.parse("2026-01-01T00:00:00Z");

        ClanMemberResponse response = new ClanMemberResponse(
                MEMBER_ID,
                ClanMember.Role.MEMBER,
                joinedAt
        );

        assertEquals(MEMBER_ID, response.getUserId());
        assertEquals(ClanMember.Role.MEMBER, response.getRole());
        assertEquals(joinedAt, response.getJoinedAt());
    }

    @Test
    void testSetters() {
        Instant joinedAt = Instant.parse("2026-01-01T00:00:00Z");
        Instant newJoinedAt = Instant.parse("2026-02-01T00:00:00Z");

        ClanMemberResponse response = new ClanMemberResponse(
                MEMBER_ID,
                ClanMember.Role.MEMBER,
                joinedAt
        );

        response.setUserId(OTHER_ID);
        response.setRole(ClanMember.Role.LEADER);
        response.setJoinedAt(newJoinedAt);

        assertEquals(OTHER_ID, response.getUserId());
        assertEquals(ClanMember.Role.LEADER, response.getRole());
        assertEquals(newJoinedAt, response.getJoinedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        Instant joinedAt = Instant.parse("2026-01-01T00:00:00Z");

        ClanMemberResponse response1 = new ClanMemberResponse(
                MEMBER_ID,
                ClanMember.Role.MEMBER,
                joinedAt
        );
        ClanMemberResponse response2 = new ClanMemberResponse(
                MEMBER_ID,
                ClanMember.Role.MEMBER,
                joinedAt
        );
        ClanMemberResponse response3 = new ClanMemberResponse(
                OTHER_ID,
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
                MEMBER_ID,
                ClanMember.Role.MEMBER,
                joinedAt
        );

        String result = response.toString();

        assertNotNull(result);
        assertTrue(result.contains(MEMBER_ID.toString()));
        assertTrue(result.contains("MEMBER"));
        assertTrue(result.contains("2026-01-01T00:00:00Z"));
    }
}
