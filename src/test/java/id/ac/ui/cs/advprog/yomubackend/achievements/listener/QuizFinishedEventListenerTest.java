package id.ac.ui.cs.advprog.yomubackend.achievements.listener;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementEventService;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.DailyMissionService;
import id.ac.ui.cs.advprog.yomubackend.shared.events.quiz.QuizFinishedEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

class QuizFinishedEventListenerTest {

    private final AchievementEventService achievementEventService = mock(AchievementEventService.class);
    private final DailyMissionService dailyMissionService = mock(DailyMissionService.class);
    private final QuizFinishedEventListener listener =
            new QuizFinishedEventListener(achievementEventService, dailyMissionService);

    @Test
    void handleQuizFinished_processesReadingAndQuizEvents() {
        UUID userId = UUID.randomUUID();

        listener.handleQuizFinished(new QuizFinishedEvent(
                userId,
                UUID.randomUUID(),
                1,
                2,
                false,
                LocalDateTime.now()
        ));

        verify(achievementEventService).processEvent(userId, AchievementEvent.READING_COMPLETED);
        verify(achievementEventService).processEvent(userId, AchievementEvent.QUIZ_FINISHED);
        verify(achievementEventService, never()).processEvent(userId, AchievementEvent.PERFECT_QUIZ_SCORE);
        verify(dailyMissionService).processDailyEvent(userId, AchievementEvent.READING_COMPLETED);
        verify(dailyMissionService).processDailyEvent(userId, AchievementEvent.QUIZ_FINISHED);
        verify(dailyMissionService, never()).processDailyEvent(userId, AchievementEvent.PERFECT_QUIZ_SCORE);
    }

    @Test
    void handleQuizFinished_processesPerfectScoreEvent() {
        UUID userId = UUID.randomUUID();

        listener.handleQuizFinished(new QuizFinishedEvent(
                userId,
                UUID.randomUUID(),
                2,
                2,
                true,
                LocalDateTime.now()
        ));

        verify(achievementEventService).processEvent(userId, AchievementEvent.PERFECT_QUIZ_SCORE);
        verify(dailyMissionService).processDailyEvent(userId, AchievementEvent.PERFECT_QUIZ_SCORE);
    }
}
