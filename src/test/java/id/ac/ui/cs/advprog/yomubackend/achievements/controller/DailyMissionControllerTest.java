package id.ac.ui.cs.advprog.yomubackend.achievements.controller;

import tools.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.DailyMissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class DailyMissionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DailyMissionService dailyMissionService;

    @InjectMocks
    private DailyMissionController dailyMissionController;

    private ObjectMapper objectMapper;

    private DailyMission dummyMission;
    private UserDailyMission dummyUserMission;
    private UUID dummyUserId;

    @BeforeEach
    void setUp() {
        // Setup Standalone yang super cepat dan anti-error library
        mockMvc = MockMvcBuilders.standaloneSetup(dailyMissionController).build();
        objectMapper = new ObjectMapper();

        dummyUserId = UUID.randomUUID();

        dummyMission = new DailyMission();
        dummyMission.setId(UUID.randomUUID());
        dummyMission.setName("Membaca 3 Artikel");
        dummyMission.setMilestone(3);
        dummyMission.setEventType("READING_COMPLETED");
        dummyMission.setIsActive(true);

        dummyUserMission = new UserDailyMission();
        dummyUserMission.setId(UUID.randomUUID());
        dummyUserMission.setUserId(dummyUserId);
        dummyUserMission.setDailyMission(dummyMission);
        dummyUserMission.setCurrentProgress(1);
        dummyUserMission.setDateAssigned(LocalDate.now());
    }

    @Test
    void testCreateDailyMission_ShouldReturn201() throws Exception {
        when(dailyMissionService.createDailyMission(any(DailyMission.class))).thenReturn(dummyMission);

        mockMvc.perform(post("/api/daily-missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyMission)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Membaca 3 Artikel"));
    }

    @Test
    void testGetActiveDailyMissions_ShouldReturn200() throws Exception {
        when(dailyMissionService.getActiveDailyMissions()).thenReturn(List.of(dummyMission));

        mockMvc.perform(get("/api/daily-missions/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Membaca 3 Artikel"));
    }

    @Test
    void testGetUserDailyMissions_ShouldReturn200() throws Exception {
        when(dailyMissionService.getUserDailyMissions(dummyUserId)).thenReturn(List.of(dummyUserMission));

        mockMvc.perform(get("/api/daily-missions/user/" + dummyUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currentProgress").value(1));
    }
}