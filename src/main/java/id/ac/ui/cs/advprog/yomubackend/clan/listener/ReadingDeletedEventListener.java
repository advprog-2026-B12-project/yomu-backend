package id.ac.ui.cs.advprog.yomubackend.clan.listener;

import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberQuizStatRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.quiz.ReadingDeletedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component("clanReadingDeletedEventListener")
public class ReadingDeletedEventListener {

    private final ClanMemberQuizStatRepository clanMemberQuizStatRepository;

    public ReadingDeletedEventListener(ClanMemberQuizStatRepository clanMemberQuizStatRepository) {
        this.clanMemberQuizStatRepository = clanMemberQuizStatRepository;
    }

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReadingDeleted(ReadingDeletedEvent event) {
        clanMemberQuizStatRepository.deleteByReadingId(event.readingId());
    }
}
