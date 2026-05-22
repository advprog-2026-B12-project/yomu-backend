package id.ac.ui.cs.advprog.yomubackend.clan.listener;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMemberQuizStat;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberQuizStatRepository;
import id.ac.ui.cs.advprog.yomubackend.shared.events.quiz.QuizFinishedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component("clanQuizFinishedEventListener")
public class QuizFinishedEventListener {

    private final ClanMemberQuizStatRepository clanMemberQuizStatRepository;

    public QuizFinishedEventListener(ClanMemberQuizStatRepository clanMemberQuizStatRepository) {
        this.clanMemberQuizStatRepository = clanMemberQuizStatRepository;
    }

    @Async("eventAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleQuizFinished(QuizFinishedEvent event) {
        ClanMemberQuizStat stat = new ClanMemberQuizStat();
        stat.setUserId(event.userId());
        stat.setReadingId(event.readingId());
        stat.setScore(event.score());
        stat.setTotal(event.total());
        stat.setCompletedAt(event.timestamp());
        clanMemberQuizStatRepository.save(stat);
    }
}
