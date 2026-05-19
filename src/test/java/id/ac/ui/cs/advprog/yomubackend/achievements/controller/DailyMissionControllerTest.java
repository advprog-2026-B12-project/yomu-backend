package id.ac.ui.cs.advprog.yomubackend.achievements.controller;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import tools.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserDailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.mapper.DailyMissionMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.DailyMissionService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DailyMissionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DailyMissionService dailyMissionService;

    @Mock
    private DailyMissionMapper dailyMissionMapper;

    @InjectMocks
    private DailyMissionController dailyMissionController;

    private ObjectMapper objectMapper;

    private DailyMission dummyMission;
    private DailyMissionResponse dummyMissionResponse;
    private UserDailyMissionResponse dummyUserMissionResponse;
    private UUID dummyUserId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dailyMissionController).build();
        objectMapper = new ObjectMapper();

        dummyUserId = UUID.randomUUID();

        dummyMission = new DailyMission();
        dummyMission.setId(UUID.randomUUID());
        dummyMission.setName("Membaca 3 Artikel");
        dummyMission.setMilestone(3);
        dummyMission.setEventType(AchievementEvent.READING_COMPLETED);
        dummyMission.setIsActive(true);

        dummyMissionResponse = new DailyMissionResponse();
        dummyMissionResponse.setId(dummyMission.getId());
        dummyMissionResponse.setName("Membaca 3 Artikel");

        dummyUserMissionResponse = new UserDailyMissionResponse();
        dummyUserMissionResponse.setId(UUID.randomUUID());
        dummyUserMissionResponse.setUserId(dummyUserId);
        dummyUserMissionResponse.setCurrentProgress(1);
        dummyUserMissionResponse.setDateAssigned(LocalDate.now());
    }

    @Test
    void testCreateDailyMission_ShouldReturn201() throws Exception {
        when(dailyMissionMapper.toEntity(any())).thenReturn(dummyMission);
        when(dailyMissionMapper.toResponse(any())).thenReturn(dummyMissionResponse);
        when(dailyMissionService.createDailyMission(any(DailyMission.class))).thenReturn(dummyMission);

        mockMvc.perform(post("/api/daily-missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyMission)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Membaca 3 Artikel"));
    }

    @Test
    void testGetActiveDailyMissions_ShouldReturn200() throws Exception {
        when(dailyMissionMapper.toResponse(any())).thenReturn(dummyMissionResponse);
        when(dailyMissionService.getActiveDailyMissions()).thenReturn(List.of(dummyMission));

        mockMvc.perform(get("/api/daily-missions/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Membaca 3 Artikel"));
    }

    @Test
    void testGetUserDailyMissions_ShouldReturn200() throws Exception {
        when(dailyMissionService.getUserDailyMissions(dummyUserId))
                .thenReturn(List.of(dummyUserMissionResponse));

        mockMvc.perform(get("/api/daily-missions/user/" + dummyUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currentProgress").value(1));
    }

    @Test
    void testUpdateDailyMission_ShouldReturn200() throws Exception {
        when(dailyMissionMapper.toEntity(any())).thenReturn(dummyMission);
        when(dailyMissionMapper.toResponse(any())).thenReturn(dummyMissionResponse);
        when(dailyMissionService.updateDailyMission(any(UUID.class), any(DailyMission.class)))
                .thenReturn(dummyMission);

        mockMvc.perform(put("/api/daily-missions/" + dummyMission.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyMission)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteDailyMission_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/daily-missions/" + dummyMission.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCreateDailyMission_NullMilestone_UsesDefault() throws Exception {
        DailyMissionRequest request = new DailyMissionRequest();
        request.setName("Read Daily");
        request.setEventType("READING_COMPLETED");
        request.setIsActive(true);
        // milestone left null -> mapToEntity defaults to 1

        DailyMission saved = new DailyMission();
        saved.setId(UUID.randomUUID());
        saved.setName("Read Daily");
        saved.setMilestone(1);
        saved.setEventType("READING_COMPLETED");
        saved.setIsActive(true);

        DailyMissionResponse savedResponse = new DailyMissionResponse();
        savedResponse.setId(saved.getId());
        savedResponse.setName("Read Daily");

        when(dailyMissionMapper.toEntity(any())).thenReturn(saved);
        when(dailyMissionMapper.toResponse(any())).thenReturn(savedResponse);
        when(dailyMissionService.createDailyMission(any(DailyMission.class))).thenReturn(saved);

        mockMvc.perform(post("/api/daily-missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Read Daily"));
    }

    @Test
    void testCreateDailyMission_InvalidEventType_ReturnsBadRequest() throws Exception {
        DailyMissionRequest request = new DailyMissionRequest();
        request.setName("Read Daily");
        request.setEventType("INVALID_EVENT");
        request.setMilestone(3);
        request.setIsActive(true);

        when(dailyMissionMapper.toEntity(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid eventType"));

        mockMvc.perform(post("/api/daily-missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateDailyMission_InvalidEventType_ReturnsBadRequest() throws Exception {
        DailyMissionRequest request = new DailyMissionRequest();
        request.setName("Read Daily");
        request.setEventType("INVALID_EVENT");
        request.setMilestone(3);
        request.setIsActive(true);

        when(dailyMissionMapper.toEntity(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid eventType"));

        mockMvc.perform(put("/api/daily-missions/" + dummyMission.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
