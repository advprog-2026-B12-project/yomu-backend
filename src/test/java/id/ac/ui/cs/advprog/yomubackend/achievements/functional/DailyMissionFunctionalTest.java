package id.ac.ui.cs.advprog.yomubackend.achievements.functional;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserDailyMissionRepository;
import id.ac.ui.cs.advprog.yomubackend.auth.model.Role;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DailyMissionFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DailyMissionRepository dailyMissionRepository;

    @Autowired
    private UserDailyMissionRepository userDailyMissionRepository;

    @Autowired
    private JwtService jwtService;

    private User student;
    private String studentToken;
    private DailyMission dailyMission;

    @BeforeEach
    void setUp() {
        userDailyMissionRepository.deleteAll();
        dailyMissionRepository.deleteAll();
        userRepository.deleteAll();

        student = new User();
        student.setUsername("studentfuncdm");
        student.setEmail("studentfuncdm@example.com");
        student.setDisplayName("Student Func DM");
        student.setPassword("password123");
        student.setRole(Role.PELAJAR);
        student = userRepository.save(student);

        studentToken = jwtService.generateToken(Map.of("role", "ROLE_PELAJAR"), student.getUsername());

        dailyMission = new DailyMission();
        dailyMission.setName("Daily Functional");
        dailyMission.setDescription("Do functional tests.");
        dailyMission.setMilestone(1);
        dailyMission.setEventType(AchievementEvent.QUIZ_FINISHED);
        dailyMission.setIsActive(true);
        dailyMission = dailyMissionRepository.save(dailyMission);
    }

    @AfterEach
    void tearDown() {
        userDailyMissionRepository.deleteAll();
        dailyMissionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testGetTodayMissions_FunctionalEndToEnd() throws Exception {
        mockMvc.perform(get("/api/daily-missions/me")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].missionId").value(dailyMission.getId().toString()))
                .andExpect(jsonPath("$[0].missionName").value("Daily Functional"))
                .andExpect(jsonPath("$[0].currentProgress").value(0))
                .andExpect(jsonPath("$[0].isCompleted").value(false));
    }
}
