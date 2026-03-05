package id.ac.ui.cs.advprog.yomubackend.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ClanMemberTest {

    @Test
    void gettersAndSetters_workCorrectly() {
        Clan clan = buildClan(1L, "Warriors");

        ClanMember member = new ClanMember();
        member.setId(10L);
        member.setUserId(99L);
        member.setClan(clan);
        member.setRole(ClanMember.Role.MEMBER);

        assertEquals(10L, member.getId());
        assertEquals(99L, member.getUserId());
        assertEquals(clan, member.getClan());
        assertEquals(ClanMember.Role.MEMBER, member.getRole());
    }

    @Test
    void defaultValues_areNullExceptJoinedAt() {
        ClanMember member = new ClanMember();

        assertNull(member.getId());
        assertNull(member.getUserId());
        assertNull(member.getClan());
        assertNull(member.getRole());
        assertNotNull(member.getJoinedAt());
    }

    @Test
    void joinedAt_isSetToNow_onInstantiation() {
        Instant before = Instant.now();
        ClanMember member = new ClanMember();
        Instant after = Instant.now();

        assertNotNull(member.getJoinedAt());
        assertFalse(member.getJoinedAt().isBefore(before));
        assertFalse(member.getJoinedAt().isAfter(after));
    }

    @Test
    void joinedAt_canBeOverridden() {
        Instant fixed = Instant.parse("2024-06-01T12:00:00Z");
        ClanMember member = new ClanMember();
        member.setJoinedAt(fixed);

        assertEquals(fixed, member.getJoinedAt());
    }

    @Test
    void role_leaderCanBeSet() {
        ClanMember member = new ClanMember();
        member.setRole(ClanMember.Role.LEADER);

        assertEquals(ClanMember.Role.LEADER, member.getRole());
    }

    @Test
    void role_memberCanBeSet() {
        ClanMember member = new ClanMember();
        member.setRole(ClanMember.Role.MEMBER);

        assertEquals(ClanMember.Role.MEMBER, member.getRole());
    }

    @Test
    void role_enumHasExactlyTwoValues() {
        ClanMember.Role[] roles = ClanMember.Role.values();

        assertEquals(2, roles.length);
        assertEquals(ClanMember.Role.LEADER, roles[0]);
        assertEquals(ClanMember.Role.MEMBER, roles[1]);
    }

    @Test
    void role_valueOfWorks() {
        assertEquals(ClanMember.Role.LEADER, ClanMember.Role.valueOf("LEADER"));
        assertEquals(ClanMember.Role.MEMBER, ClanMember.Role.valueOf("MEMBER"));
    }

    @Test
    void equals_returnsTrueForSameValues() {
        Instant now = Instant.now();
        Clan clan = buildClan(1L, "Warriors");

        ClanMember m1 = new ClanMember();
        m1.setId(1L);
        m1.setUserId(5L);
        m1.setClan(clan);
        m1.setRole(ClanMember.Role.MEMBER);
        m1.setJoinedAt(now);

        ClanMember m2 = new ClanMember();
        m2.setId(1L);
        m2.setUserId(5L);
        m2.setClan(clan);
        m2.setRole(ClanMember.Role.MEMBER);
        m2.setJoinedAt(now);

        assertEquals(m1, m2);
    }

    @Test
    void equals_returnsFalseForDifferentUserId() {
        Instant now = Instant.now();
        Clan clan = buildClan(1L, "Warriors");

        ClanMember m1 = new ClanMember();
        m1.setId(1L);
        m1.setUserId(5L);
        m1.setClan(clan);
        m1.setRole(ClanMember.Role.MEMBER);
        m1.setJoinedAt(now);

        ClanMember m2 = new ClanMember();
        m2.setId(1L);
        m2.setUserId(99L);
        m2.setClan(clan);
        m2.setRole(ClanMember.Role.MEMBER);
        m2.setJoinedAt(now);

        assertNotEquals(m1, m2);
    }

    @Test
    void equals_returnsFalseForDifferentRole() {
        Instant now = Instant.now();
        Clan clan = buildClan(1L, "Warriors");

        ClanMember m1 = new ClanMember();
        m1.setId(1L);
        m1.setUserId(5L);
        m1.setClan(clan);
        m1.setRole(ClanMember.Role.LEADER);
        m1.setJoinedAt(now);

        ClanMember m2 = new ClanMember();
        m2.setId(1L);
        m2.setUserId(5L);
        m2.setClan(clan);
        m2.setRole(ClanMember.Role.MEMBER);
        m2.setJoinedAt(now);

        assertNotEquals(m1, m2);
    }

    @Test
    void hashCode_isSameForEqualObjects() {
        Instant now = Instant.now();
        Clan clan = buildClan(1L, "Warriors");

        ClanMember m1 = new ClanMember();
        m1.setId(1L);
        m1.setUserId(5L);
        m1.setClan(clan);
        m1.setRole(ClanMember.Role.MEMBER);
        m1.setJoinedAt(now);

        ClanMember m2 = new ClanMember();
        m2.setId(1L);
        m2.setUserId(5L);
        m2.setClan(clan);
        m2.setRole(ClanMember.Role.MEMBER);
        m2.setJoinedAt(now);

        assertEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    void toString_containsRelevantFields() {
        Clan clan = buildClan(1L, "Warriors");

        ClanMember member = new ClanMember();
        member.setId(10L);
        member.setUserId(99L);
        member.setClan(clan);
        member.setRole(ClanMember.Role.LEADER);

        String str = member.toString();
        assertTrue(str.contains("10"));
        assertTrue(str.contains("99"));
        assertTrue(str.contains("LEADER"));
    }

    // ────────────────────────────────────────────────
    // Helper
    // ────────────────────────────────────────────────

    private Clan buildClan(Long id, String name) {
        Clan clan = new Clan();
        clan.setId(id);
        clan.setName(name);
        clan.setLeaderUserId(1L);
        return clan;
    }
}