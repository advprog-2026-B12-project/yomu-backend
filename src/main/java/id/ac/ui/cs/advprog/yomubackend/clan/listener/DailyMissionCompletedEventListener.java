package id.ac.ui.cs.advprog.yomubackend.clan.listener;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMemberDailyMissionCompletion;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberDailyMissionCompletionRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.achievements.DailyMissionCompletedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class DailyMissionCompletedEventListener {

    private final ClanMemberDailyMissionCompletionRepository completionRepository;

    public DailyMissionCompletedEventListener(ClanMemberDailyMissionCompletionRepository completionRepository) {
        this.completionRepository = completionRepository;
    }

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleDailyMissionCompleted(DailyMissionCompletedEvent event) {
        if (completionRepository.existsByUserIdAndDateAssigned(event.userId(), event.date())) {
            return;
        }
        ClanMemberDailyMissionCompletion completion = new ClanMemberDailyMissionCompletion();
        completion.setUserId(event.userId());
        completion.setDateAssigned(event.date());
        completionRepository.save(completion);
    }
}
