package id.ac.ui.cs.advprog.yomubackend.achievements.functional;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserAchievementRepository;
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
public class AchievementFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @Autowired
    private JwtService jwtService;

    private User student;
    private String studentToken;
    private Achievement achievement;

    @BeforeEach
    void setUp() {
        userAchievementRepository.deleteAll();
        achievementRepository.deleteAll();
        userRepository.deleteAll();

        student = new User();
        student.setUsername("studentfunc");
        student.setEmail("studentfunc@example.com");
        student.setDisplayName("Student Func");
        student.setPassword("password123");
        student.setRole(Role.PELAJAR);
        student = userRepository.save(student);

        studentToken = jwtService.generateToken(Map.of("role", "ROLE_PELAJAR"), student.getUsername());

        achievement = new Achievement();
        achievement.setName("Functional Reader");
        achievement.setDescription("Read functionally.");
        achievement.setPoints(10);
        achievement.setMilestone(2);
        achievement.setEventType(AchievementEvent.READING_COMPLETED);
        achievement = achievementRepository.save(achievement);
    }

    @AfterEach
    void tearDown() {
        userAchievementRepository.deleteAll();
        achievementRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testGetAchievementsProgress_FunctionalEndToEnd() throws Exception {
        // Assert progress returns the initialized achievement with 0 progress
        mockMvc.perform(get("/api/achievements/user/" + student.getId() + "/progress")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].achievementId").value(achievement.getId().toString()))
                .andExpect(jsonPath("$[0].name").value("Functional Reader"))
                .andExpect(jsonPath("$[0].currentProgress").value(0))
                .andExpect(jsonPath("$[0].isUnlocked").value(false));
    }
}
