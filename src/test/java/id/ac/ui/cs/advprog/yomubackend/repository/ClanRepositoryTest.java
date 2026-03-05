package id.ac.ui.cs.advprog.yomubackend.repository;

import id.ac.ui.cs.advprog.yomubackend.entity.Clan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanRepositoryTest {

    @Mock
    private ClanRepository clanRepository;

    // ────────────────────────────────────────────────
    // save
    // ────────────────────────────────────────────────

    @Test
    void save_returnsSavedClan() {
        Clan clan = buildClan(null, "Warriors", "desc", 1L);
        Clan saved = buildClan(1L, "Warriors", "desc", 1L);
        when(clanRepository.save(clan)).thenReturn(saved);

        Clan result = clanRepository.save(clan);

        assertNotNull(result.getId());
        assertEquals("Warriors", result.getName());
        verify(clanRepository).save(clan);
    }

    // ────────────────────────────────────────────────
    // findById
    // ────────────────────────────────────────────────

    @Test
    void findById_returnsPresent_whenClanExists() {
        Clan clan = buildClan(1L, "Warriors", "desc", 1L);
        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));

        Optional<Clan> result = clanRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Warriors", result.get().getName());
    }

    @Test
    void findById_returnsEmpty_whenNotExists() {
        when(clanRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Clan> result = clanRepository.findById(999L);

        assertTrue(result.isEmpty());
    }

    // ────────────────────────────────────────────────
    // findAll
    // ────────────────────────────────────────────────

    @Test
    void findAll_returnsAllClans() {
        List<Clan> clans = List.of(
                buildClan(1L, "Warriors", "desc", 1L),
                buildClan(2L, "Rangers", "desc", 2L)
        );
        when(clanRepository.findAll()).thenReturn(clans);

        List<Clan> result = clanRepository.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findAll_returnsEmptyList_whenNoClans() {
        when(clanRepository.findAll()).thenReturn(List.of());

        List<Clan> result = clanRepository.findAll();

        assertTrue(result.isEmpty());
    }

    // ────────────────────────────────────────────────
    // delete
    // ────────────────────────────────────────────────

    @Test
    void delete_callsRepositoryDelete() {
        Clan clan = buildClan(1L, "Warriors", "desc", 1L);

        clanRepository.delete(clan);

        verify(clanRepository, times(1)).delete(clan);
    }

    @Test
    void deleteById_callsRepositoryDeleteById() {
        clanRepository.deleteById(1L);

        verify(clanRepository, times(1)).deleteById(1L);
    }

    // ────────────────────────────────────────────────
    // existsByNameIgnoreCase
    // ────────────────────────────────────────────────

    @Test
    void existsByNameIgnoreCase_returnsTrue_forExactMatch() {
        when(clanRepository.existsByNameIgnoreCase("Warriors")).thenReturn(true);

        assertTrue(clanRepository.existsByNameIgnoreCase("Warriors"));
    }

    @Test
    void existsByNameIgnoreCase_returnsTrue_forLowercase() {
        when(clanRepository.existsByNameIgnoreCase("warriors")).thenReturn(true);

        assertTrue(clanRepository.existsByNameIgnoreCase("warriors"));
    }

    @Test
    void existsByNameIgnoreCase_returnsTrue_forUppercase() {
        when(clanRepository.existsByNameIgnoreCase("WARRIORS")).thenReturn(true);

        assertTrue(clanRepository.existsByNameIgnoreCase("WARRIORS"));
    }

    @Test
    void existsByNameIgnoreCase_returnsTrue_forMixedCase() {
        when(clanRepository.existsByNameIgnoreCase("wArRiOrS")).thenReturn(true);

        assertTrue(clanRepository.existsByNameIgnoreCase("wArRiOrS"));
    }

    @Test
    void existsByNameIgnoreCase_returnsFalse_whenNotExists() {
        when(clanRepository.existsByNameIgnoreCase("Nonexistent")).thenReturn(false);

        assertFalse(clanRepository.existsByNameIgnoreCase("Nonexistent"));
    }

    @Test
    void existsByNameIgnoreCase_passesNameCorrectlyToRepository() {
        when(clanRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);

        clanRepository.existsByNameIgnoreCase("Warriors");

        verify(clanRepository).existsByNameIgnoreCase("Warriors");
    }

    // ────────────────────────────────────────────────
    // Helper
    // ────────────────────────────────────────────────

    private Clan buildClan(Long id, String name, String description, Long leaderUserId) {
        Clan clan = new Clan();
        clan.setId(id);
        clan.setName(name);
        clan.setDescription(description);
        clan.setLeaderUserId(leaderUserId);
        return clan;
    }
}