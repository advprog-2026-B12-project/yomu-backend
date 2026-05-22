package id.ac.ui.cs.advprog.yomubackend.achievements.listener;

import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserDailyMissionRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.auth.UserDeletedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AchievementsUserDeletedEventListener {

    private final UserAchievementRepository userAchievementRepository;
    private final UserDailyMissionRepository userDailyMissionRepository;

    public AchievementsUserDeletedEventListener(UserAchievementRepository userAchievementRepository,
                                                UserDailyMissionRepository userDailyMissionRepository) {
        this.userAchievementRepository = userAchievementRepository;
        this.userDailyMissionRepository = userDailyMissionRepository;
    }

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleUserDeleted(UserDeletedEvent event) {
        userAchievementRepository.deleteByUserId(event.userId());
        userDailyMissionRepository.deleteByUserId(event.userId());
    }
}
