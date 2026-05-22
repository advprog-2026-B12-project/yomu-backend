package id.ac.ui.cs.advprog.yomubackend.achievements.listener;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementEventService;
import id.ac.ui.cs.advprog.yomubackend.shared.events.auth.UserLoggedInEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

class UserLoggedInEventListenerTest {

    private final AchievementEventService achievementEventService = mock(AchievementEventService.class);
    private final UserLoggedInEventListener listener =
            new UserLoggedInEventListener(achievementEventService);

    @Test
    void handleUserLoggedIn_triggersLoginStreakEvent() {
        UUID userId = UUID.randomUUID();
        UserLoggedInEvent event = new UserLoggedInEvent(userId, LocalDateTime.now());

        listener.handleUserLoggedIn(event);

        verify(achievementEventService).processEvent(userId, AchievementEvent.LOGIN_STREAK);
        verifyNoMoreInteractions(achievementEventService);
    }
}
