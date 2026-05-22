package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserDailyMissionRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyMissionBuffModifierTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-05-07T12:00:00Z"), ZoneOffset.UTC);
    private static final LocalDate TODAY = LocalDate.of(2026, 5, 7);

    @Mock
    private UserDailyMissionRepository userDailyMissionRepository;

    private DailyMissionBuffModifier modifier;

    @BeforeEach
    void setUp() {
        modifier = new DailyMissionBuffModifier(userDailyMissionRepository, FIXED_CLOCK);
    }

    @Test
    void calculateMultiplier_shouldReturnNeutral_whenMembersIsNull() {
        assertEquals(1.0, modifier.calculateMultiplier(null));
    }

    @Test
    void calculateMultiplier_shouldReturnNeutral_whenMembersIsEmpty() {
        assertEquals(1.0, modifier.calculateMultiplier(List.of()));
    }

    @Test
    void getModifierName_shouldReturnProductivityBuff() {
        assertEquals("Productivity Buff", modifier.getModifierName());
    }

    private ClanMember member(long id) {
        Clan clan = new Clan();
        clan.setId(1L);
        ClanMember m = new ClanMember();
        m.setClan(clan);
        m.setUserId(new UUID(0L, id));
        m.setRole(ClanMember.Role.MEMBER);
        return m;
    }
}
