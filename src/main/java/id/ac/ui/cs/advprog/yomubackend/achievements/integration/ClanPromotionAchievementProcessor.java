package id.ac.ui.cs.advprog.yomubackend.achievements.integration;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementEventService;
import id.ac.ui.cs.advprog.yomubackend.clan.completion.ClanPromotion;
import id.ac.ui.cs.advprog.yomubackend.clan.completion.ClanPromotionProcessor;
import org.springframework.stereotype.Component;

@Component
public class ClanPromotionAchievementProcessor implements ClanPromotionProcessor {

    private final AchievementEventService achievementEventService;

    public ClanPromotionAchievementProcessor(AchievementEventService achievementEventService) {
        this.achievementEventService = achievementEventService;
    }

    @Override
    public void processPromotion(ClanPromotion promotion) {
        promotion.memberIds().forEach(memberId ->
                achievementEventService.processEvent(memberId, AchievementEvent.CLAN_PROMOTION)
        );
    }
}
