package id.ac.ui.cs.advprog.yomubackend.achievements.controller;

import tools.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AchievementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AchievementService achievementService;

    @InjectMocks
    private AchievementController achievementController;

    private ObjectMapper objectMapper;

    private Achievement dummyAchievement;
    private UserAchievement dummyUserAchievement;
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
        dummyAchievement.setEventType("READING_COMPLETED");

        dummyUserAchievement = new UserAchievement();
        dummyUserAchievement.setId(UUID.randomUUID());
        dummyUserAchievement.setUserId(dummyUserId);
        dummyUserAchievement.setAchievement(dummyAchievement);
        dummyUserAchievement.setCurrentProgress(5);
    }

    @Test
    void testCreateAchievement_ShouldReturn201() throws Exception {
        when(achievementService.createAchievement(any(Achievement.class))).thenReturn(dummyAchievement);

        mockMvc.perform(post("/api/achievements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyAchievement)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Kutu Buku"));
    }

    @Test
    void testGetAllAchievements_ShouldReturn200() throws Exception {
        when(achievementService.getAllAchievements()).thenReturn(List.of(dummyAchievement));

        mockMvc.perform(get("/api/achievements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Kutu Buku"));
    }

    @Test
    void testGetUserAchievements_ShouldReturn200() throws Exception {
        when(achievementService.getUserAchievements(dummyUserId)).thenReturn(List.of(dummyUserAchievement));

        mockMvc.perform(get("/api/achievements/user/" + dummyUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currentProgress").value(5));
    }

    @Test
    void testToggleDisplayAchievement_ShouldReturn200() throws Exception {
        dummyUserAchievement.setIsDisplayed(true);
        when(achievementService.toggleDisplayAchievement(dummyUserAchievement.getId())).thenReturn(dummyUserAchievement);

        mockMvc.perform(put("/api/achievements/display/" + dummyUserAchievement.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDisplayed").value(true));
    }
}