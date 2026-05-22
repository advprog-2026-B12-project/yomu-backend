package id.ac.ui.cs.advprog.yomubackend.achievements.integration;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementEventService;
import id.ac.ui.cs.advprog.yomubackend.clan.completion.ClanPromotion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class ClanPromotionAchievementProcessorTest {

    private final AchievementEventService achievementEventService = mock(AchievementEventService.class);
    private final ClanPromotionAchievementProcessor processor =
            new ClanPromotionAchievementProcessor(achievementEventService);

    @Test
    void processPromotion_triggersEventForEachMember() {
        UUID member1 = UUID.randomUUID();
        UUID member2 = UUID.randomUUID();

        ClanPromotion promotion = new ClanPromotion(1L, List.of(member1, member2), "GOLD", LocalDateTime.now());

        processor.processPromotion(promotion);

        verify(achievementEventService).processEvent(member1, AchievementEvent.CLAN_PROMOTION);
        verify(achievementEventService).processEvent(member2, AchievementEvent.CLAN_PROMOTION);
        verifyNoMoreInteractions(achievementEventService);
    }

    @Test
    void processPromotion_noMembers_noInteractions() {
        ClanPromotion promotion = new ClanPromotion(1L, List.of(), "SILVER", LocalDateTime.now());

        processor.processPromotion(promotion);

        verifyNoInteractions(achievementEventService);
    }
}
