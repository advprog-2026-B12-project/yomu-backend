package id.ac.ui.cs.advprog.yomubackend.achievements.integration;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementEventService;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.DailyMissionService;
import id.ac.ui.cs.advprog.yomubackend.quiz.completion.QuizCompletion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class QuizCompletionAchievementProcessorTest {

    private final AchievementEventService achievementEventService = mock(AchievementEventService.class);
    private final DailyMissionService dailyMissionService = mock(DailyMissionService.class);
    private final QuizCompletionAchievementProcessor processor =
            new QuizCompletionAchievementProcessor(achievementEventService, dailyMissionService);

    @Test
    void processCompletion_processesReadingAndQuizEvents() {
        UUID userId = UUID.randomUUID();

        processor.processCompletion(new QuizCompletion(
                userId,
                UUID.randomUUID(),
                1,
                2,
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
    void processCompletion_processesPerfectScoreEvent() {
        UUID userId = UUID.randomUUID();

        processor.processCompletion(new QuizCompletion(
                userId,
                UUID.randomUUID(),
                2,
                2,
                LocalDateTime.now()
        ));

        verify(achievementEventService).processEvent(userId, AchievementEvent.PERFECT_QUIZ_SCORE);
        verify(dailyMissionService).processDailyEvent(userId, AchievementEvent.PERFECT_QUIZ_SCORE);
    }
}
