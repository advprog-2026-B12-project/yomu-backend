package id.ac.ui.cs.advprog.yomubackend.clan.repository;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanRepositoryTest {

    private static final UUID LEADER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID OTHER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Mock
    private ClanRepository clanRepository;

    @Test
    void save_returnsSavedClan() {
        Clan clan = buildClan(null, "Warriors", "desc", LEADER_ID);
        Clan saved = buildClan(1L, "Warriors", "desc", LEADER_ID);

        when(clanRepository.save(clan)).thenReturn(saved);

        Clan result = clanRepository.save(clan);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Warriors", result.getName());
        assertEquals("desc", result.getDescription());
        assertEquals(LEADER_ID, result.getLeaderUserId());
        verify(clanRepository).save(clan);
    }

    @Test
    void findById_returnsPresent_whenClanExists() {
        Clan clan = buildClan(1L, "Warriors", "desc", LEADER_ID);

        when(clanRepository.findById(1L)).thenReturn(Optional.of(clan));

        Optional<Clan> result = clanRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Warriors", result.get().getName());
        verify(clanRepository).findById(1L);
    }

    @Test
    void findById_returnsEmpty_whenClanDoesNotExist() {
        when(clanRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Clan> result = clanRepository.findById(999L);

        assertTrue(result.isEmpty());
        verify(clanRepository).findById(999L);
    }

    @Test
    void findAll_returnsAllClans() {
        List<Clan> clans = List.of(
                buildClan(1L, "Warriors", "desc", LEADER_ID),
                buildClan(2L, "Rangers", "desc", OTHER_ID)
        );

        when(clanRepository.findAll()).thenReturn(clans);

        List<Clan> result = clanRepository.findAll();

        assertEquals(2, result.size());
        assertEquals("Warriors", result.get(0).getName());
        assertEquals("Rangers", result.get(1).getName());
        verify(clanRepository).findAll();
    }

    @Test
    void findAll_returnsEmptyList_whenNoClansExist() {
        when(clanRepository.findAll()).thenReturn(List.of());

        List<Clan> result = clanRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(clanRepository).findAll();
    }

    @Test
    void delete_callsRepositoryDelete() {
        Clan clan = buildClan(1L, "Warriors", "desc", LEADER_ID);

        clanRepository.delete(clan);

        verify(clanRepository, times(1)).delete(clan);
    }

    @Test
    void deleteById_callsRepositoryDeleteById() {
        clanRepository.deleteById(1L);

        verify(clanRepository, times(1)).deleteById(1L);
    }

    @Test
    void existsByName_returnsTrue_whenClanNameExists() {
        when(clanRepository.existsByName("Warriors")).thenReturn(true);

        boolean result = clanRepository.existsByName("Warriors");

        assertTrue(result);
        verify(clanRepository).existsByName("Warriors");
    }

    @Test
    void existsByName_returnsFalse_whenClanNameDoesNotExist() {
        when(clanRepository.existsByName("Nonexistent")).thenReturn(false);

        boolean result = clanRepository.existsByName("Nonexistent");

        assertFalse(result);
        verify(clanRepository).existsByName("Nonexistent");
    }

    @Test
    void existsByName_passesNameCorrectlyToRepository() {
        when(clanRepository.existsByName(anyString())).thenReturn(false);

        clanRepository.existsByName("Warriors");

        verify(clanRepository).existsByName("Warriors");
    }

    private Clan buildClan(Long id, String name, String description, UUID leaderUserId) {
        Clan clan = new Clan();
        clan.setId(id);
        clan.setName(name);
        clan.setDescription(description);
        clan.setLeaderUserId(leaderUserId);
        return clan;
    }
}
