package id.ac.ui.cs.advprog.yomubackend.achievements.integration;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementEventService;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.DailyMissionService;
import id.ac.ui.cs.advprog.yomubackend.quiz.completion.QuizCompletion;
import id.ac.ui.cs.advprog.yomubackend.quiz.completion.QuizCompletionProcessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class QuizCompletionAchievementProcessor implements QuizCompletionProcessor {

    private final AchievementEventService achievementEventService;
    private final DailyMissionService dailyMissionService;

    public QuizCompletionAchievementProcessor(AchievementEventService achievementEventService,
                                              DailyMissionService dailyMissionService) {
        this.achievementEventService = achievementEventService;
        this.dailyMissionService = dailyMissionService;
    }

    @Override
    public void processCompletion(QuizCompletion completion) {
        processEvent(completion.userId(), AchievementEvent.READING_COMPLETED);
        processEvent(completion.userId(), AchievementEvent.QUIZ_FINISHED);

        if (completion.isPerfectScore()) {
            processEvent(completion.userId(), AchievementEvent.PERFECT_QUIZ_SCORE);
        }
    }

    private void processEvent(UUID userId, String eventType) {
        achievementEventService.processEvent(userId, eventType);
        dailyMissionService.processDailyEvent(userId, eventType);
    }
}
