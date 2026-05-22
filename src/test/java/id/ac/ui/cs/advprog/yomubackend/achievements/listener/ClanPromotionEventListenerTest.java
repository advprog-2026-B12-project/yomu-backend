package id.ac.ui.cs.advprog.yomubackend.achievements.listener;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementEventService;
import id.ac.ui.cs.advprog.yomubackend.shared.events.clan.ClanPromotionEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class ClanPromotionEventListenerTest {

    private final AchievementEventService achievementEventService = mock(AchievementEventService.class);
    private final ClanPromotionEventListener listener =
            new ClanPromotionEventListener(achievementEventService);

    @Test
    void handleClanPromotion_triggersEventForEachMember() {
        UUID member1 = UUID.randomUUID();
        UUID member2 = UUID.randomUUID();

        ClanPromotionEvent event = new ClanPromotionEvent(1L, List.of(member1, member2), "GOLD", LocalDateTime.now());

        listener.handleClanPromotion(event);

        verify(achievementEventService).processEvent(member1, AchievementEvent.CLAN_PROMOTION);
        verify(achievementEventService).processEvent(member2, AchievementEvent.CLAN_PROMOTION);
        verifyNoMoreInteractions(achievementEventService);
    }

    @Test
    void handleClanPromotion_noMembers_noInteractions() {
        ClanPromotionEvent event = new ClanPromotionEvent(1L, List.of(), "SILVER", LocalDateTime.now());

        listener.handleClanPromotion(event);

        verifyNoInteractions(achievementEventService);
    }
}
