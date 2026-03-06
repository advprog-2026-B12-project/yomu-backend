package id.ac.ui.cs.advprog.yomubackend.service;

import id.ac.ui.cs.advprog.yomubackend.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomubackend.repository.ClanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanServiceTest {

    @Mock
    private ClanRepository clanRepository;

    @Mock
    private ClanMemberRepository clanMemberRepository;

    @InjectMocks
    private ClanService clanService;

    private Clan savedClan;

    @BeforeEach
    void setUp() {
        savedClan = new Clan();
        savedClan.setId(1L);
        savedClan.setName("Warriors");
        savedClan.setDescription("Fight together");
        savedClan.setLeaderUserId(42L);
    }

    // ════════════════════════════════════════════════
    // createClan — happy path
    // ════════════════════════════════════════════════

    @Test
    void createClan_returnsPersistedClan() {
        when(clanMemberRepository.existsByUserId(42L)).thenReturn(false);
        when(clanRepository.existsByNameIgnoreCase("Warriors")).thenReturn(false);
        when(clanRepository.save(any(Clan.class))).thenReturn(savedClan);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(i -> i.getArgument(0));

        Clan result = clanService.createClan(42L, "Warriors", "Fight together");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Warriors", result.getName());
        assertEquals(42L, result.getLeaderUserId());
    }

    @Test
    void createClan_trimsClanName_beforeSaving() {
        when(clanMemberRepository.existsByUserId(anyLong())).thenReturn(false);
        when(clanRepository.existsByNameIgnoreCase("Warriors")).thenReturn(false);
        when(clanRepository.save(any(Clan.class))).thenReturn(savedClan);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(i -> i.getArgument(0));

        clanService.createClan(42L, "  Warriors  ", "desc");

        ArgumentCaptor<Clan> captor = ArgumentCaptor.forClass(Clan.class);
        verify(clanRepository).save(captor.capture());
        assertEquals("Warriors", captor.getValue().getName());
    }

    @Test
    void createClan_setsLeaderUserId() {
        when(clanMemberRepository.existsByUserId(anyLong())).thenReturn(false);
        when(clanRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(clanRepository.save(any(Clan.class))).thenReturn(savedClan);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(i -> i.getArgument(0));

        clanService.createClan(42L, "Warriors", "desc");

        ArgumentCaptor<Clan> captor = ArgumentCaptor.forClass(Clan.class);
        verify(clanRepository).save(captor.capture());
        assertEquals(42L, captor.getValue().getLeaderUserId());
    }

    @Test
    void createClan_savesLeaderClanMemberWithLeaderRole() {
        when(clanMemberRepository.existsByUserId(anyLong())).thenReturn(false);
        when(clanRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(clanRepository.save(any(Clan.class))).thenReturn(savedClan);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(i -> i.getArgument(0));

        clanService.createClan(42L, "Warriors", "desc");

        ArgumentCaptor<ClanMember> captor = ArgumentCaptor.forClass(ClanMember.class);
        verify(clanMemberRepository).save(captor.capture());
        assertEquals(ClanMember.Role.LEADER, captor.getValue().getRole());
        assertEquals(42L, captor.getValue().getUserId());
        assertEquals(savedClan, captor.getValue().getClan());
    }

    @Test
    void createClan_savesDescriptionAsProvided() {
        when(clanMemberRepository.existsByUserId(anyLong())).thenReturn(false);
        when(clanRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(clanRepository.save(any(Clan.class))).thenReturn(savedClan);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(i -> i.getArgument(0));

        clanService.createClan(42L, "Warriors", "Fight together");

        ArgumentCaptor<Clan> captor = ArgumentCaptor.forClass(Clan.class);
        verify(clanRepository).save(captor.capture());
        assertEquals("Fight together", captor.getValue().getDescription());
    }

    @Test
    void createClan_allowsNullDescription() {
        when(clanMemberRepository.existsByUserId(anyLong())).thenReturn(false);
        when(clanRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(clanRepository.save(any(Clan.class))).thenReturn(savedClan);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> clanService.createClan(42L, "Warriors", null));
    }

    // ════════════════════════════════════════════════
    // createClan — validation errors
    // ════════════════════════════════════════════════

    @Test
    void createClan_throwsIllegalArgument_whenUserIdIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clanService.createClan(null, "Warriors", "desc"));

        assertEquals("userId is required", ex.getMessage());
        verifyNoInteractions(clanRepository, clanMemberRepository);
    }

    @Test
    void createClan_throwsIllegalArgument_whenNameIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clanService.createClan(42L, null, "desc"));

        assertEquals("name is required", ex.getMessage());
        verifyNoInteractions(clanRepository, clanMemberRepository);
    }

    @Test
    void createClan_throwsIllegalArgument_whenNameIsBlank() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clanService.createClan(42L, "   ", "desc"));

        assertEquals("name is required", ex.getMessage());
        verifyNoInteractions(clanRepository, clanMemberRepository);
    }

    @Test
    void createClan_throwsIllegalArgument_whenNameIsEmpty() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clanService.createClan(42L, "", "desc"));

        assertEquals("name is required", ex.getMessage());
    }

    @Test
    void createClan_throwsIllegalState_whenUserAlreadyInClan() {
        when(clanMemberRepository.existsByUserId(42L)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> clanService.createClan(42L, "Warriors", "desc"));

        assertEquals("User already in a clan", ex.getMessage());
        verifyNoInteractions(clanRepository);
    }

    @Test
    void createClan_throwsIllegalState_whenClanNameAlreadyExists() {
        when(clanMemberRepository.existsByUserId(42L)).thenReturn(false);
        when(clanRepository.existsByNameIgnoreCase("Warriors")).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> clanService.createClan(42L, "Warriors", "desc"));

        assertEquals("Clan name already exists", ex.getMessage());
        verify(clanRepository, never()).save(any());
    }

    // ════════════════════════════════════════════════
    // joinClan — happy path
    // ════════════════════════════════════════════════

    @Test
    void joinClan_returnsSavedClanMember() {
        when(clanMemberRepository.existsByUserId(7L)).thenReturn(false);
        when(clanRepository.findById(1L)).thenReturn(Optional.of(savedClan));
        when(clanMemberRepository.existsByClan_IdAndUserId(1L, 7L)).thenReturn(false);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(i -> i.getArgument(0));

        ClanMember result = clanService.joinClan(7L, 1L);

        assertNotNull(result);
        assertEquals(7L, result.getUserId());
        assertEquals(ClanMember.Role.MEMBER, result.getRole());
        assertEquals(savedClan, result.getClan());
    }

    @Test
    void joinClan_savesMemberWithMemberRole() {
        when(clanMemberRepository.existsByUserId(anyLong())).thenReturn(false);
        when(clanRepository.findById(1L)).thenReturn(Optional.of(savedClan));
        when(clanMemberRepository.existsByClan_IdAndUserId(anyLong(), anyLong())).thenReturn(false);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(i -> i.getArgument(0));

        clanService.joinClan(7L, 1L);

        ArgumentCaptor<ClanMember> captor = ArgumentCaptor.forClass(ClanMember.class);
        verify(clanMemberRepository).save(captor.capture());
        assertEquals(ClanMember.Role.MEMBER, captor.getValue().getRole());
    }

    @Test
    void joinClan_savesMemberWithCorrectClan() {
        when(clanMemberRepository.existsByUserId(anyLong())).thenReturn(false);
        when(clanRepository.findById(1L)).thenReturn(Optional.of(savedClan));
        when(clanMemberRepository.existsByClan_IdAndUserId(anyLong(), anyLong())).thenReturn(false);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(i -> i.getArgument(0));

        clanService.joinClan(7L, 1L);

        ArgumentCaptor<ClanMember> captor = ArgumentCaptor.forClass(ClanMember.class);
        verify(clanMemberRepository).save(captor.capture());
        assertEquals(savedClan, captor.getValue().getClan());
        assertEquals(7L, captor.getValue().getUserId());
    }

    // ════════════════════════════════════════════════
    // joinClan — validation errors
    // ════════════════════════════════════════════════

    @Test
    void joinClan_throwsIllegalArgument_whenUserIdIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clanService.joinClan(null, 1L));

        assertEquals("userId is required", ex.getMessage());
        verifyNoInteractions(clanRepository, clanMemberRepository);
    }

    @Test
    void joinClan_throwsIllegalArgument_whenClanIdIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clanService.joinClan(7L, null));

        assertEquals("clanId is required", ex.getMessage());
        verifyNoInteractions(clanRepository, clanMemberRepository);
    }

    @Test
    void joinClan_throwsIllegalState_whenUserAlreadyInClan() {
        when(clanMemberRepository.existsByUserId(7L)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> clanService.joinClan(7L, 1L));

        assertEquals("User already in a clan", ex.getMessage());
        verifyNoInteractions(clanRepository);
    }

    @Test
    void joinClan_throwsIllegalArgument_whenClanNotFound() {
        when(clanMemberRepository.existsByUserId(7L)).thenReturn(false);
        when(clanRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clanService.joinClan(7L, 999L));

        assertEquals("Clan not found", ex.getMessage());
        verify(clanMemberRepository, never()).save(any());
    }

    @Test
    void joinClan_throwsIllegalState_whenUserAlreadyInThisClan() {
        when(clanMemberRepository.existsByUserId(7L)).thenReturn(false);
        when(clanRepository.findById(1L)).thenReturn(Optional.of(savedClan));
        when(clanMemberRepository.existsByClan_IdAndUserId(1L, 7L)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> clanService.joinClan(7L, 1L));

        assertEquals("Already joined this clan", ex.getMessage());
        verify(clanMemberRepository, never()).save(any());
    }

    // ════════════════════════════════════════════════
    // Interaction counts
    // ════════════════════════════════════════════════

    @Test
    void createClan_callsSaveOnBothRepositoriesExactlyOnce() {
        when(clanMemberRepository.existsByUserId(anyLong())).thenReturn(false);
        when(clanRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(clanRepository.save(any(Clan.class))).thenReturn(savedClan);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(i -> i.getArgument(0));

        clanService.createClan(42L, "Warriors", "desc");

        verify(clanRepository, times(1)).save(any(Clan.class));
        verify(clanMemberRepository, times(1)).save(any(ClanMember.class));
    }

    @Test
    void joinClan_callsClanMemberSaveExactlyOnce() {
        when(clanMemberRepository.existsByUserId(anyLong())).thenReturn(false);
        when(clanRepository.findById(1L)).thenReturn(Optional.of(savedClan));
        when(clanMemberRepository.existsByClan_IdAndUserId(anyLong(), anyLong())).thenReturn(false);
        when(clanMemberRepository.save(any(ClanMember.class))).thenAnswer(i -> i.getArgument(0));

        clanService.joinClan(7L, 1L);

        verify(clanMemberRepository, times(1)).save(any(ClanMember.class));
    }
}