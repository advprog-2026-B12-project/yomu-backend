package id.ac.ui.cs.advprog.yomubackend.achievements.listener;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementEventService;
import id.ac.ui.cs.advprog.yomubackend.shared.events.auth.UserLoggedInEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserLoggedInEventListener {

    private final AchievementEventService achievementEventService;

    public UserLoggedInEventListener(AchievementEventService achievementEventService) {
        this.achievementEventService = achievementEventService;
    }

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleUserLoggedIn(UserLoggedInEvent event) {
        achievementEventService.processEvent(event.userId(), AchievementEvent.LOGIN_STREAK);
    }
}
