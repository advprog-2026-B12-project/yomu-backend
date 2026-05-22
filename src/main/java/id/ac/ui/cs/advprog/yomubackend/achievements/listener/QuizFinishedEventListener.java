package id.ac.ui.cs.advprog.yomubackend.achievements.listener;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementEventService;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.DailyMissionService;
import id.ac.ui.cs.advprog.yomubackend.shared.events.quiz.QuizFinishedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
public class QuizFinishedEventListener {

    private final AchievementEventService achievementEventService;
    private final DailyMissionService dailyMissionService;

    public QuizFinishedEventListener(AchievementEventService achievementEventService,
                                     DailyMissionService dailyMissionService) {
        this.achievementEventService = achievementEventService;
        this.dailyMissionService = dailyMissionService;
    }

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleQuizFinished(QuizFinishedEvent event) {
        processEvent(event.userId(), AchievementEvent.READING_COMPLETED);
        processEvent(event.userId(), AchievementEvent.QUIZ_FINISHED);

        if (event.isPerfectScore()) {
            processEvent(event.userId(), AchievementEvent.PERFECT_QUIZ_SCORE);
        }
    }

    private void processEvent(UUID userId, String eventType) {
        achievementEventService.processEvent(userId, eventType);
        dailyMissionService.processDailyEvent(userId, eventType);
    }
}
