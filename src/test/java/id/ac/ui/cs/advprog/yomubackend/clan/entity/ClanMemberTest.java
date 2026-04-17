package id.ac.ui.cs.advprog.yomubackend.clan.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ClanMemberTest {

    @Test
    void gettersAndSetters_shouldWork() {
        Instant joinedAt = Instant.parse("2026-01-02T00:00:00Z");

        Clan clan = new Clan();
        clan.setId(1L);
        clan.setName("Warriors");

        ClanMember member = new ClanMember();
        member.setId(10L);
        member.setClan(clan);
        member.setUserId(42L);
        member.setRole(ClanMember.Role.LEADER);
        member.setJoinedAt(joinedAt);

        assertEquals(10L, member.getId());
        assertEquals(clan, member.getClan());
        assertEquals(42L, member.getUserId());
        assertEquals(ClanMember.Role.LEADER, member.getRole());
        assertEquals(joinedAt, member.getJoinedAt());
    }

    @Test
    void defaultValues_shouldBeNullInitially() {
        ClanMember member = new ClanMember();

        assertNull(member.getId());
        assertNull(member.getClan());
        assertNull(member.getUserId());
        assertNull(member.getRole());
        assertNull(member.getJoinedAt());
    }

    @Test
    void onCreate_shouldSetJoinedAt_whenNull() throws Exception {
        ClanMember member = new ClanMember();
        assertNull(member.getJoinedAt());

        Method onCreate = ClanMember.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(member);

        assertNotNull(member.getJoinedAt());
    }

    @Test
    void onCreate_shouldNotOverrideJoinedAt_whenAlreadySet() throws Exception {
        Instant joinedAt = Instant.parse("2026-01-02T00:00:00Z");

        ClanMember member = new ClanMember();
        member.setJoinedAt(joinedAt);

        Method onCreate = ClanMember.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(member);

        assertEquals(joinedAt, member.getJoinedAt());
    }

    @Test
    void equals_shouldBeTrue_forSameReference() {
        ClanMember member = new ClanMember();
        member.setId(1L);

        assertEquals(member, member);
    }

    @Test
    void equals_shouldBeFalse_forDifferentInstancesEvenIfValuesMatch() {
        Clan clan1 = new Clan();
        clan1.setId(1L);

        Clan clan2 = new Clan();
        clan2.setId(1L);

        ClanMember member1 = new ClanMember();
        member1.setId(10L);
        member1.setClan(clan1);
        member1.setUserId(42L);
        member1.setRole(ClanMember.Role.LEADER);

        ClanMember member2 = new ClanMember();
        member2.setId(10L);
        member2.setClan(clan2);
        member2.setUserId(42L);
        member2.setRole(ClanMember.Role.LEADER);

        assertNotEquals(member1, member2);
    }

    @Test
    void hashCode_shouldBeStable_forSameReference() {
        ClanMember member = new ClanMember();
        member.setId(10L);

        assertEquals(member.hashCode(), member.hashCode());
    }

    @Test
    void toString_shouldReturnNonNullString() {
        ClanMember member = new ClanMember();
        member.setId(10L);
        member.setUserId(42L);
        member.setRole(ClanMember.Role.LEADER);

        String result = member.toString();

        assertNotNull(result);
        assertTrue(result.contains("ClanMember"));
    }
}