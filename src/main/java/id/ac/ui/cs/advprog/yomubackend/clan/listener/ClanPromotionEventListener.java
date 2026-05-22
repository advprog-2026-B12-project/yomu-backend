package id.ac.ui.cs.advprog.yomubackend.clan.listener;

import id.ac.ui.cs.advprog.yomubackend.clan.completion.ClanPromotion;
import id.ac.ui.cs.advprog.yomubackend.clan.completion.ClanPromotionProcessor;
import id.ac.ui.cs.advprog.yomubackend.shared.events.clan.ClanPromotionEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
public class ClanPromotionEventListener {

    private final List<ClanPromotionProcessor> processors;

    public ClanPromotionEventListener(List<ClanPromotionProcessor> processors) {
        this.processors = processors;
    }

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleClanPromotion(ClanPromotionEvent event) {
        ClanPromotion promotion = new ClanPromotion(
                event.clanId(),
                event.memberIds(),
                event.newDivision(),
                event.timestamp()
        );
        processors.forEach(processor -> processor.processPromotion(promotion));
    }
}
