package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserDailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.exception.DailyMissionNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.achievements.mapper.UserDailyMissionMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserDailyMissionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class DailyMissionServiceImpl implements DailyMissionService {

    private final DailyMissionRepository dailyMissionRepository;
    private final UserDailyMissionRepository userDailyMissionRepository;
    private final UserDailyMissionMapper userDailyMissionMapper;

    public DailyMissionServiceImpl(DailyMissionRepository dailyMissionRepository,
                                   UserDailyMissionRepository userDailyMissionRepository,
                                   UserDailyMissionMapper userDailyMissionMapper) {
        this.dailyMissionRepository = dailyMissionRepository;
        this.userDailyMissionRepository = userDailyMissionRepository;
        this.userDailyMissionMapper = userDailyMissionMapper;
    }

    @Override
    @Transactional
    public List<String> processDailyEvent(UUID userId, String eventType) {
        List<DailyMission> activeMissions = dailyMissionRepository.findByEventTypeAndIsActiveTrue(eventType);
        List<String> completedMissionNames = new ArrayList<>();
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
                completedMissionNames.add(mission.getName());
            }

            userDailyMissionRepository.save(userProgress);
        }

        return completedMissionNames;
    }

    @Override
    public DailyMission createDailyMission(DailyMission mission) {
        return dailyMissionRepository.save(mission);
    }

    @Override
    public List<DailyMission> getAllDailyMissions() {
        return dailyMissionRepository.findAll();
    }

    @Override
    public List<DailyMission> getActiveDailyMissions() {
        return dailyMissionRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDailyMissionResponse> getUserDailyMissions(UUID userId) {
        return userDailyMissionRepository.findByUserIdAndDateAssigned(userId, LocalDate.now()).stream()
                .map(userDailyMissionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDailyMissionResponse> getTodayMissionsWithProgress(UUID userId) {
        List<DailyMission> activeMissions = dailyMissionRepository.findByIsActiveTrue();
        LocalDate today = LocalDate.now();

        return activeMissions.stream()
                .map(mission -> {
                    UserDailyMission progress = userDailyMissionRepository
                            .findByUserIdAndDailyMissionIdAndDateAssigned(userId, mission.getId(), today)
                            .orElseGet(() -> {
                                UserDailyMission empty = new UserDailyMission();
                                empty.setUserId(userId);
                                empty.setDailyMission(mission);
                                empty.setDateAssigned(today);
                                empty.setCurrentProgress(0);
                                empty.setIsCompleted(false);
                                return empty;
                            });
                    return userDailyMissionMapper.toResponse(progress);
                })
                .toList();
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void rotateDailyMissions() {
        List<DailyMission> allMissions = dailyMissionRepository.findAll();
        if (allMissions.isEmpty()) {
            return;
        }
        allMissions.sort(Comparator.comparing(DailyMission::getId));
        int index = (int) (LocalDate.now().toEpochDay() % allMissions.size());
        for (DailyMission mission : allMissions) {
            mission.setIsActive(false);
        }
        allMissions.get(index).setIsActive(true);
        dailyMissionRepository.saveAll(allMissions);
    }

    @Override
    public DailyMission updateDailyMission(UUID id, DailyMission updatedMission) {
        DailyMission mission = dailyMissionRepository.findById(id)
                .orElseThrow(() -> new DailyMissionNotFoundException(id));
        mission.setName(updatedMission.getName());
        mission.setDescription(updatedMission.getDescription());
        mission.setMilestone(updatedMission.getMilestone());
        mission.setEventType(updatedMission.getEventType());
        if (updatedMission.getIsActive() != null) {
            mission.setIsActive(updatedMission.getIsActive());
        }
        return dailyMissionRepository.save(mission);
    }

    @Override
    @Transactional
    public void deleteDailyMission(UUID id) {
        if (!dailyMissionRepository.existsById(id)) {
            throw new DailyMissionNotFoundException(id);
        }
        userDailyMissionRepository.deleteByDailyMissionId(id);
        dailyMissionRepository.deleteById(id);
    }
}
