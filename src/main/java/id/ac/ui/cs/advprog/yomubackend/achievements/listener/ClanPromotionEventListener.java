package id.ac.ui.cs.advprog.yomubackend.achievements.listener;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementEventService;
import id.ac.ui.cs.advprog.yomubackend.shared.events.clan.ClanPromotionEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ClanPromotionEventListener {

    private final AchievementEventService achievementEventService;

    public ClanPromotionEventListener(AchievementEventService achievementEventService) {
        this.achievementEventService = achievementEventService;
    }

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleClanPromotion(ClanPromotionEvent event) {
        event.memberIds().forEach(memberId ->
                achievementEventService.processEvent(memberId, AchievementEvent.CLAN_PROMOTION)
        );
    }
}
