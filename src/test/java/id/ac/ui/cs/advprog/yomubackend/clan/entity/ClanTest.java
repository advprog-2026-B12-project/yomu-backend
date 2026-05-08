package id.ac.ui.cs.advprog.yomubackend.clan.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClanTest {

    private static final UUID LEADER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void gettersAndSetters_shouldWork() {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

        Clan clan = new Clan();
        clan.setId(1L);
        clan.setName("Warriors");
        clan.setDescription("Fight together");
        clan.setLeaderUserId(LEADER_ID);
        clan.setDivision("BRONZE");
        clan.setCreatedAt(createdAt);

        assertEquals(1L, clan.getId());
        assertEquals("Warriors", clan.getName());
        assertEquals("Fight together", clan.getDescription());
        assertEquals(LEADER_ID, clan.getLeaderUserId());
        assertEquals("BRONZE", clan.getDivision());
        assertEquals(createdAt, clan.getCreatedAt());
    }

    @Test
    void defaultValues_shouldBeNullInitially() {
        Clan clan = new Clan();

        assertNull(clan.getName());
        assertNull(clan.getDescription());
        assertEquals("BRONZE", clan.getDivision()); // bukan assertNull
    }

    @Test
    void onCreate_shouldSetCreatedAt_whenNull() throws Exception {
        Clan clan = new Clan();
        assertNull(clan.getCreatedAt());

        Method onCreate = Clan.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(clan);

        assertNotNull(clan.getCreatedAt());
    }

    @Test
    void onCreate_shouldNotOverrideCreatedAt_whenAlreadySet() throws Exception {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

        Clan clan = new Clan();
        clan.setCreatedAt(createdAt);

        Method onCreate = Clan.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(clan);

        assertEquals(createdAt, clan.getCreatedAt());
    }

    @Test
    void equals_shouldBeTrue_forSameReference() {
        Clan clan = new Clan();
        clan.setId(1L);

        assertEquals(clan, clan);
    }

    @Test
    void equals_shouldBeFalse_forDifferentInstancesEvenIfValuesMatch() {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

        Clan clan1 = new Clan();
        clan1.setId(1L);
        clan1.setName("Warriors");
        clan1.setDescription("Fight together");
        clan1.setLeaderUserId(LEADER_ID);
        clan1.setDivision("BRONZE");
        clan1.setCreatedAt(createdAt);

        Clan clan2 = new Clan();
        clan2.setId(1L);
        clan2.setName("Warriors");
        clan2.setDescription("Fight together");
        clan2.setLeaderUserId(LEADER_ID);
        clan2.setDivision("BRONZE");
        clan2.setCreatedAt(createdAt);

        assertNotEquals(clan1, clan2);
    }

    @Test
    void hashCode_shouldBeStable_forSameReference() {
        Clan clan = new Clan();
        clan.setId(1L);

        assertEquals(clan.hashCode(), clan.hashCode());
    }

    @Test
    void toString_shouldReturnNonNullString() {
        Clan clan = new Clan();
        clan.setId(1L);
        clan.setName("Warriors");

        String result = clan.toString();

        assertNotNull(result);
        assertTrue(result.contains("Clan"));
    }
}
