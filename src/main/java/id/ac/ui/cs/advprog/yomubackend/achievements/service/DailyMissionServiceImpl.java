package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserDailyMissionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class DailyMissionServiceImpl implements DailyMissionService {

    private final DailyMissionRepository dailyMissionRepository;
    private final UserDailyMissionRepository userDailyMissionRepository;

    @Autowired
    public DailyMissionServiceImpl(DailyMissionRepository dailyMissionRepository,
                                   UserDailyMissionRepository userDailyMissionRepository) {
        this.dailyMissionRepository = dailyMissionRepository;
        this.userDailyMissionRepository = userDailyMissionRepository;
    }

    @Override
    @Transactional
    public void processDailyEvent(UUID userId, String eventType) {
        List<DailyMission> activeMissions = dailyMissionRepository.findByEventTypeAndIsActiveTrue(eventType);

        LocalDate today = LocalDate.now();

        for (DailyMission mission : activeMissions) {
            UserDailyMission userProgress = userDailyMissionRepository
                    .findByUserIdAndDailyMissionIdAndDateAssigned(userId, mission.getId(), today)
                    .orElseGet(() -> {
                        UserDailyMission newProgress = new UserDailyMission();
                        newProgress.setUserId(userId);
                        newProgress.setDailyMission(mission);
                        newProgress.setDateAssigned(today);
                        newProgress.setCurrentProgress(0);
                        newProgress.setIsCompleted(false);
                        return newProgress;
                    });

            if (userProgress.getIsCompleted()) {
                continue;
            }

            userProgress.setCurrentProgress(userProgress.getCurrentProgress() + 1);

            if (userProgress.getCurrentProgress() >= mission.getMilestone()) {
                userProgress.setIsCompleted(true);
                userProgress.setCompletedAt(LocalDateTime.now());
            }

            userDailyMissionRepository.save(userProgress);
        }
    }

    @Override
    public DailyMission createDailyMission(DailyMission mission) {
        return dailyMissionRepository.save(mission);
    }

    @Override
    public List<DailyMission> getActiveDailyMissions() {
        return dailyMissionRepository.findByIsActiveTrue();
    }

    @Override
    public List<UserDailyMission> getUserDailyMissions(UUID userId) {
        return userDailyMissionRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void rotateDailyMissions() {
        List<DailyMission> allMissions = dailyMissionRepository.findAll();

        if (allMissions.isEmpty()) {
            return;
        }

        for (DailyMission mission : allMissions) {
            mission.setIsActive(false);
        }

        Collections.shuffle(allMissions);

        allMissions.getFirst().setIsActive(true);

        dailyMissionRepository.saveAll(allMissions);
    }
}