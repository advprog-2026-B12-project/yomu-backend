package id.ac.ui.cs.advprog.yomubackend.clan.league;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMemberQuizStat;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberQuizStatRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Primary
public class QuizAttemptMemberStatProvider implements MemberStatProvider {

    private final ClanMemberQuizStatRepository clanMemberQuizStatRepository;

    public QuizAttemptMemberStatProvider(ClanMemberQuizStatRepository clanMemberQuizStatRepository) {
        this.clanMemberQuizStatRepository = clanMemberQuizStatRepository;
    }

    @Override
    public MemberStat getStatForUser(UUID userId) {
        List<ClanMemberQuizStat> stats = clanMemberQuizStatRepository.findByUserId(userId);

        int totalScore = stats.stream().mapToInt(ClanMemberQuizStat::getScore).sum();
        int totalQuestions = stats.stream().mapToInt(ClanMemberQuizStat::getTotal).sum();
        int quizCount = stats.size();
        double accuracy = totalQuestions == 0 ? 0.0 : (double) totalScore / totalQuestions;

        return new MemberStat(userId, totalScore, quizCount, accuracy);
    }
}
