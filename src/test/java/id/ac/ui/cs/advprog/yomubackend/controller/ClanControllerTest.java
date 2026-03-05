package id.ac.ui.cs.advprog.yomubackend.controller;

import id.ac.ui.cs.advprog.yomubackend.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.yomubackend.dto.JoinClanRequest;
import id.ac.ui.cs.advprog.yomubackend.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.repository.ClanRepository;
import id.ac.ui.cs.advprog.yomubackend.service.ClanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClanControllerTest {

    @Mock
    private ClanRepository clanRepository;

    @Mock
    private ClanService clanService;

    @InjectMocks
    private ClanController clanController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clanController).build();
    }

    // ────────────────────────────────────────────────
    // GET /api/clans
    // ────────────────────────────────────────────────

    @Test
    void listClans_returnsEmptyList_whenNoClansExist() throws Exception {
        when(clanRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/clans"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(clanRepository, times(1)).findAll();
    }

    @Test
    void listClans_returnsAllClans() throws Exception {
        Clan clan1 = buildClan(1L, "Alpha", "Alpha desc", 10L);
        Clan clan2 = buildClan(2L, "Beta",  "Beta desc",  20L);
        when(clanRepository.findAll()).thenReturn(List.of(clan1, clan2));

        mockMvc.perform(get("/api/clans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alpha"))
                .andExpect(jsonPath("$[0].leaderUserId").value(10))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Beta"))
                .andExpect(jsonPath("$[1].leaderUserId").value(20));

        verify(clanRepository, times(1)).findAll();
    }

    @Test
    void listClans_returnsSingleClan() throws Exception {
        Clan clan = buildClan(10L, "Solo Clan", "Only one", 99L);
        when(clanRepository.findAll()).thenReturn(List.of(clan));

        mockMvc.perform(get("/api/clans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Solo Clan"))
                .andExpect(jsonPath("$[0].description").value("Only one"))
                .andExpect(jsonPath("$[0].leaderUserId").value(99));
    }

    // ────────────────────────────────────────────────
    // POST /api/clans
    // ────────────────────────────────────────────────

    @Test
    void createClan_returnsCreatedClan() throws Exception {
        CreateClanRequest req = buildCreateRequest(42L, "Warriors", "Fight together");
        Clan created = buildClan(1L, "Warriors", "Fight together", 42L);
        when(clanService.createClan(42L, "Warriors", "Fight together")).thenReturn(created);

        mockMvc.perform(post("/api/clans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Warriors"))
                .andExpect(jsonPath("$.description").value("Fight together"))
                .andExpect(jsonPath("$.leaderUserId").value(42));

        verify(clanService, times(1)).createClan(42L, "Warriors", "Fight together");
    }

    @Test
    void createClan_delegatesCorrectFieldsToService() throws Exception {
        CreateClanRequest req = buildCreateRequest(99L, "Rangers", "Scout ahead");
        Clan created = buildClan(5L, "Rangers", "Scout ahead", 99L);
        when(clanService.createClan(anyLong(), anyString(), anyString())).thenReturn(created);

        mockMvc.perform(post("/api/clans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isOk());

        verify(clanService).createClan(eq(99L), eq("Rangers"), eq("Scout ahead"));
    }

    @Test
    void createClan_withNullDescription_stillCallsService() throws Exception {
        CreateClanRequest req = buildCreateRequest(1L, "Nameless", null);
        Clan created = buildClan(3L, "Nameless", null, 1L);
        when(clanService.createClan(1L, "Nameless", null)).thenReturn(created);

        mockMvc.perform(post("/api/clans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nameless"))
                .andExpect(jsonPath("$.description").doesNotExist());
    }

    // ────────────────────────────────────────────────
    // POST /api/clans/{clanId}/join
    // ────────────────────────────────────────────────

    @Test
    void joinClan_returnsClanMember() throws Exception {
        JoinClanRequest req = buildJoinRequest(7L);
        Clan clan = buildClan(10L, "Warriors", "Fight together", 1L);
        ClanMember member = buildClanMember(1L, 7L, clan, ClanMember.Role.MEMBER);
        when(clanService.joinClan(7L, 10L)).thenReturn(member);

        mockMvc.perform(post("/api/clans/10/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(7))
                .andExpect(jsonPath("$.clan.id").value(10))
                .andExpect(jsonPath("$.role").value("MEMBER"));

        verify(clanService, times(1)).joinClan(7L, 10L);
    }

    @Test
    void joinClan_usesClanIdFromPathVariable() throws Exception {
        JoinClanRequest req = buildJoinRequest(3L);
        Clan clan = buildClan(99L, "Clan99", "desc", 1L);
        ClanMember member = buildClanMember(2L, 3L, clan, ClanMember.Role.MEMBER);
        when(clanService.joinClan(anyLong(), anyLong())).thenReturn(member);

        mockMvc.perform(post("/api/clans/99/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isOk());

        // clanId harus berasal dari path variable, bukan body
        verify(clanService).joinClan(eq(3L), eq(99L));
    }

    @Test
    void joinClan_leaderRole_isReflectedInResponse() throws Exception {
        JoinClanRequest req = buildJoinRequest(5L);
        Clan clan = buildClan(1L, "Founders", "First clan", 5L);
        ClanMember member = buildClanMember(1L, 5L, clan, ClanMember.Role.LEADER);
        when(clanService.joinClan(5L, 1L)).thenReturn(member);

        mockMvc.perform(post("/api/clans/1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("LEADER"))
                .andExpect(jsonPath("$.userId").value(5));
    }

    @Test
    void joinClan_withDifferentUsers_callsServiceEachTime() throws Exception {
        Clan clan = buildClan(5L, "Mixed", "desc", 1L);
        ClanMember m1 = buildClanMember(10L, 1L, clan, ClanMember.Role.MEMBER);
        ClanMember m2 = buildClanMember(11L, 2L, clan, ClanMember.Role.MEMBER);

        when(clanService.joinClan(1L, 5L)).thenReturn(m1);
        when(clanService.joinClan(2L, 5L)).thenReturn(m2);

        mockMvc.perform(post("/api/clans/5/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(buildJoinRequest(1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));

        mockMvc.perform(post("/api/clans/5/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(buildJoinRequest(2L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2));

        verify(clanService, times(1)).joinClan(1L, 5L);
        verify(clanService, times(1)).joinClan(2L, 5L);
    }

    // ────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────

    private Clan buildClan(Long id, String name, String description, Long leaderUserId) {
        Clan clan = new Clan();
        clan.setId(id);
        clan.setName(name);
        clan.setDescription(description);
        clan.setLeaderUserId(leaderUserId);
        return clan;
    }

    private ClanMember buildClanMember(Long id, Long userId, Clan clan, ClanMember.Role role) {
        ClanMember member = new ClanMember();
        member.setId(id);
        member.setUserId(userId);
        member.setClan(clan);
        member.setRole(role);
        return member;
    }

    private CreateClanRequest buildCreateRequest(Long userId, String name, String description) {
        CreateClanRequest req = new CreateClanRequest();
        req.setUserId(userId);
        req.setName(name);
        req.setDescription(description);
        return req;
    }

    private JoinClanRequest buildJoinRequest(Long userId) {
        JoinClanRequest req = new JoinClanRequest();
        req.setUserId(userId);
        return req;
    }

    /** Serialize a CreateClanRequest to a JSON string without ObjectMapper. */
    private String toJson(CreateClanRequest req) {
        String desc = req.getDescription() == null ? "null" : "\"" + req.getDescription() + "\"";
        return String.format("{\"userId\":%d,\"name\":\"%s\",\"description\":%s}",
                req.getUserId(), req.getName(), desc);
    }

    /** Serialize a JoinClanRequest to a JSON string without ObjectMapper. */
    private String toJson(JoinClanRequest req) {
        return String.format("{\"userId\":%d}", req.getUserId());
    }
}