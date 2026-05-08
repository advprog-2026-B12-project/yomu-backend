package id.ac.ui.cs.advprog.yomubackend.clan.league;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class DummyMemberStatProvider implements MemberStatProvider {

    @Override
    public MemberStat getStatForUser(UUID userId) {
        int bucket = Math.abs(userId.hashCode());
        int totalScore = ((bucket % 5) + 1) * 100;
        int quizCount = ((bucket % 3) + 1) * 3;
        double accuracy = 0.5 + ((bucket % 5) * 0.1);

        return new MemberStat(userId, totalScore, quizCount, accuracy);
    }
}