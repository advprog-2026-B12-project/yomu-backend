package id.ac.ui.cs.advprog.yomubackend.clan.listener;

import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanJoinRequestRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberDailyMissionCompletionRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberQuizStatRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.auth.UserDeletedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ClanUserDeletedEventListener {

    private final ClanMemberRepository clanMemberRepository;
    private final ClanJoinRequestRepository clanJoinRequestRepository;
    private final ClanMemberQuizStatRepository clanMemberQuizStatRepository;
    private final ClanMemberDailyMissionCompletionRepository completionRepository;

    public ClanUserDeletedEventListener(ClanMemberRepository clanMemberRepository,
                                        ClanJoinRequestRepository clanJoinRequestRepository,
                                        ClanMemberQuizStatRepository clanMemberQuizStatRepository,
                                        ClanMemberDailyMissionCompletionRepository completionRepository) {
        this.clanMemberRepository = clanMemberRepository;
        this.clanJoinRequestRepository = clanJoinRequestRepository;
        this.clanMemberQuizStatRepository = clanMemberQuizStatRepository;
        this.completionRepository = completionRepository;
    }

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleUserDeleted(UserDeletedEvent event) {
        clanMemberRepository.deleteByUserId(event.userId());
        clanJoinRequestRepository.deleteByUserId(event.userId());
        clanMemberQuizStatRepository.deleteByUserId(event.userId());
        completionRepository.deleteByUserId(event.userId());
    }
}
