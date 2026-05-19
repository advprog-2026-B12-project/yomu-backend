package id.ac.ui.cs.advprog.yomubackend.achievements.controller;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementEventService;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementService;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.DailyMissionService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementRequest;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AchievementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AchievementService achievementService;

    @Mock
    private AchievementEventService achievementEventService;

    @Mock
    private DailyMissionService dailyMissionService;

    @Mock
    private AchievementMapper achievementMapper;

    @InjectMocks
    private AchievementController achievementController;

    private ObjectMapper objectMapper;

    private Achievement dummyAchievement;
    private AchievementResponse dummyAchievementResponse;
    private UserAchievementResponse dummyUserAchievementResponse;
    private UUID dummyUserId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(achievementController).build();
        objectMapper = new ObjectMapper();

        dummyUserId = UUID.randomUUID();

        dummyAchievement = new Achievement();
        dummyAchievement.setId(UUID.randomUUID());
        dummyAchievement.setName("Kutu Buku");
        dummyAchievement.setMilestone(10);
        dummyAchievement.setEventType(AchievementEvent.READING_COMPLETED);

        dummyAchievementResponse = new AchievementResponse();
        dummyAchievementResponse.setId(dummyAchievement.getId());
        dummyAchievementResponse.setName("Kutu Buku");

        dummyUserAchievementResponse = new UserAchievementResponse();
        dummyUserAchievementResponse.setId(UUID.randomUUID());
        dummyUserAchievementResponse.setUserId(dummyUserId);
        dummyUserAchievementResponse.setCurrentProgress(5);
    }

    @Test
    void testCreateAchievement_ShouldReturn201() throws Exception {
        when(achievementMapper.toEntity(any())).thenReturn(dummyAchievement);
        when(achievementMapper.toResponse(any())).thenReturn(dummyAchievementResponse);
        when(achievementService.createAchievement(any(Achievement.class))).thenReturn(dummyAchievement);

        mockMvc.perform(post("/api/achievements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyAchievement)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Kutu Buku"));
    }

    @Test
    void testGetAllAchievements_ShouldReturn200() throws Exception {
        when(achievementMapper.toResponse(any())).thenReturn(dummyAchievementResponse);
        when(achievementService.getAllAchievements()).thenReturn(List.of(dummyAchievement));

        mockMvc.perform(get("/api/achievements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Kutu Buku"));
    }

    @Test
    void testGetUserAchievements_ShouldReturn200() throws Exception {
        when(achievementService.getUserAchievements(dummyUserId))
                .thenReturn(List.of(dummyUserAchievementResponse));

        mockMvc.perform(get("/api/achievements/user/" + dummyUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currentProgress").value(5));
    }

    @Test
    void testGetUserAchievementProgress_ShouldReturn200() throws Exception {
        AchievementProgressResponse progressResponse = new AchievementProgressResponse();
        progressResponse.setAchievementId(dummyAchievement.getId());
        progressResponse.setName(dummyAchievement.getName());
        progressResponse.setCurrentProgress(5);
        progressResponse.setIsUnlocked(false);

        when(achievementService.getUserAchievementProgress(dummyUserId)).thenReturn(List.of(progressResponse));

        mockMvc.perform(get("/api/achievements/user/" + dummyUserId + "/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].achievementId").value(dummyAchievement.getId().toString()))
                .andExpect(jsonPath("$[0].currentProgress").value(5));
    }

    @Test
    void testToggleDisplayAchievement_ShouldReturn200() throws Exception {
        dummyUserAchievementResponse.setIsDisplayed(true);
        UUID userAchievementId = dummyUserAchievementResponse.getId();
        when(achievementService.toggleDisplayAchievement(userAchievementId))
                .thenReturn(dummyUserAchievementResponse);

        mockMvc.perform(put("/api/achievements/display/" + userAchievementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDisplayed").value(true));
    }

    @Test
    void testTriggerEvent_ShouldReturn200WithUnlockedAchievements() throws Exception {
        AchievementProgressResponse unlockedAch = new AchievementProgressResponse();
        unlockedAch.setAchievementId(dummyAchievement.getId());
        unlockedAch.setName(dummyAchievement.getName());
        unlockedAch.setIsUnlocked(true);

        when(achievementEventService.processEvent(any(UUID.class), anyString()))
                .thenReturn(List.of(unlockedAch));
        when(dailyMissionService.processDailyEvent(any(UUID.class), anyString()))
                .thenReturn(List.of("Membaca Berita"));

        String body = "{\"userId\":\"" + dummyUserId + "\", \"eventType\":\"READING_COMPLETED\"}";

        mockMvc.perform(post("/api/achievements/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unlockedAchievements").isArray())
                .andExpect(jsonPath("$.unlockedAchievements[0].name").value("Kutu Buku"))
                .andExpect(jsonPath("$.unlockedAchievements[0].isUnlocked").value(true))
                .andExpect(jsonPath("$.completedDailyMissions").isArray())
                .andExpect(jsonPath("$.completedDailyMissions[0]").value("Membaca Berita"));
    }

    @Test
    void testTriggerEvent_ShouldReturnEmptyLists_WhenNothingUnlocked() throws Exception {
        when(achievementEventService.processEvent(any(UUID.class), anyString()))
                .thenReturn(List.of());
        when(dailyMissionService.processDailyEvent(any(UUID.class), anyString()))
                .thenReturn(List.of());

        String body = "{\"userId\":\"" + dummyUserId + "\", \"eventType\":\"READING_COMPLETED\"}";

        mockMvc.perform(post("/api/achievements/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unlockedAchievements").isArray())
                .andExpect(jsonPath("$.unlockedAchievements").isEmpty())
                .andExpect(jsonPath("$.completedDailyMissions").isArray())
                .andExpect(jsonPath("$.completedDailyMissions").isEmpty());
    }

    @Test
    void testCreateAchievement_NullPointsAndMilestone_UsesDefaults() throws Exception {
        AchievementRequest request = new AchievementRequest();
        request.setName("Test Achievement");
        request.setEventType("READING_COMPLETED");
        // points and milestone are null -> mapToEntity defaults to 0 and 1

        Achievement saved = new Achievement();
        saved.setId(UUID.randomUUID());
        saved.setName("Test Achievement");
        saved.setPoints(0);
        saved.setMilestone(1);
        saved.setEventType("READING_COMPLETED");

        AchievementResponse savedResponse = new AchievementResponse();
        savedResponse.setId(saved.getId());
        savedResponse.setName("Test Achievement");

        when(achievementMapper.toEntity(any())).thenReturn(saved);
        when(achievementMapper.toResponse(any())).thenReturn(savedResponse);
        when(achievementService.createAchievement(any(Achievement.class))).thenReturn(saved);

        mockMvc.perform(post("/api/achievements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Achievement"));
    }

    @Test
    void testCreateAchievement_InvalidEventType_ReturnsBadRequest() throws Exception {
        AchievementRequest request = new AchievementRequest();
        request.setName("Test");
        request.setEventType("INVALID_EVENT");
        request.setPoints(5);
        request.setMilestone(3);

        when(achievementMapper.toEntity(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid eventType"));

        mockMvc.perform(post("/api/achievements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTriggerEvent_InvalidEventType_ReturnsBadRequest() throws Exception {
        String body = "{\"userId\":\"" + dummyUserId + "\", \"eventType\":\"INVALID_EVENT\"}";

        mockMvc.perform(post("/api/achievements/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
