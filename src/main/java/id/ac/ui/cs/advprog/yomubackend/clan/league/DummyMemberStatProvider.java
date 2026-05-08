package id.ac.ui.cs.advprog.yomubackend.clan.league;

import org.springframework.stereotype.Service;

@Service
public class DummyMemberStatProvider implements MemberStatProvider {

    @Override
    public MemberStat getStatForUser(Long userId) {
        int totalScore = (int) ((userId % 5) + 1) * 100;
        int quizCount = (int) ((userId % 3) + 1) * 3;
        double accuracy = 0.5 + ((userId % 5) * 0.1);

        return new MemberStat(userId, totalScore, quizCount, accuracy);
    }
}