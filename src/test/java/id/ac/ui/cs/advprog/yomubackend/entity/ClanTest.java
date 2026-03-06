package id.ac.ui.cs.advprog.yomubackend.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ClanTest {

    @Test
    void gettersAndSetters_workCorrectly() {
        Clan clan = new Clan();
        clan.setId(1L);
        clan.setName("Warriors");
        clan.setDescription("Fight together");
        clan.setLeaderUserId(42L);

        assertEquals(1L, clan.getId());
        assertEquals("Warriors", clan.getName());
        assertEquals("Fight together", clan.getDescription());
        assertEquals(42L, clan.getLeaderUserId());
    }

    @Test
    void defaultValues_areNullExceptCreatedAt() {
        Clan clan = new Clan();

        assertNull(clan.getId());
        assertNull(clan.getName());
        assertNull(clan.getDescription());
        assertNull(clan.getLeaderUserId());
        assertNotNull(clan.getCreatedAt());
    }

    @Test
    void createdAt_isSetToNow_onInstantiation() {
        Instant before = Instant.now();
        Clan clan = new Clan();
        Instant after = Instant.now();

        assertNotNull(clan.getCreatedAt());
        assertFalse(clan.getCreatedAt().isBefore(before));
        assertFalse(clan.getCreatedAt().isAfter(after));
    }

    @Test
    void createdAt_canBeOverridden() {
        Instant fixed = Instant.parse("2024-01-01T00:00:00Z");
        Clan clan = new Clan();
        clan.setCreatedAt(fixed);

        assertEquals(fixed, clan.getCreatedAt());
    }

    @Test
    void description_canBeNull() {
        Clan clan = new Clan();
        clan.setName("No Desc Clan");
        clan.setLeaderUserId(1L);
        clan.setDescription(null);

        assertNull(clan.getDescription());
    }

    @Test
    void equals_returnsTrueForSameValues() {
        Instant now = Instant.now();

        Clan c1 = new Clan();
        c1.setId(1L);
        c1.setName("Warriors");
        c1.setDescription("desc");
        c1.setLeaderUserId(10L);
        c1.setCreatedAt(now);

        Clan c2 = new Clan();
        c2.setId(1L);
        c2.setName("Warriors");
        c2.setDescription("desc");
        c2.setLeaderUserId(10L);
        c2.setCreatedAt(now);

        assertEquals(c1, c2);
    }

    @Test
    void equals_returnsFalseForDifferentId() {
        Instant now = Instant.now();

        Clan c1 = new Clan();
        c1.setId(1L);
        c1.setName("Warriors");
        c1.setCreatedAt(now);

        Clan c2 = new Clan();
        c2.setId(2L);
        c2.setName("Warriors");
        c2.setCreatedAt(now);

        assertNotEquals(c1, c2);
    }

    @Test
    void equals_returnsFalseForDifferentName() {
        Instant now = Instant.now();

        Clan c1 = new Clan();
        c1.setId(1L);
        c1.setName("Warriors");
        c1.setCreatedAt(now);

        Clan c2 = new Clan();
        c2.setId(1L);
        c2.setName("Rangers");
        c2.setCreatedAt(now);

        assertNotEquals(c1, c2);
    }

    @Test
    void hashCode_isSameForEqualObjects() {
        Instant now = Instant.now();

        Clan c1 = new Clan();
        c1.setId(1L);
        c1.setName("Warriors");
        c1.setLeaderUserId(10L);
        c1.setCreatedAt(now);

        Clan c2 = new Clan();
        c2.setId(1L);
        c2.setName("Warriors");
        c2.setLeaderUserId(10L);
        c2.setCreatedAt(now);

        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void toString_containsAllFields() {
        Clan clan = new Clan();
        clan.setId(1L);
        clan.setName("Warriors");
        clan.setDescription("Fight together");
        clan.setLeaderUserId(42L);

        String str = clan.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("Warriors"));
        assertTrue(str.contains("Fight together"));
        assertTrue(str.contains("42"));
    }
}