package id.ac.ui.cs.advprog.yomubackend.clan.league;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMemberQuizStat;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberQuizStatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizAttemptMemberStatProviderTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Mock
    private ClanMemberQuizStatRepository clanMemberQuizStatRepository;

    @Test
    void getStatForUser_shouldAggregateQuizStats() {
        QuizAttemptMemberStatProvider provider = new QuizAttemptMemberStatProvider(clanMemberQuizStatRepository);

        when(clanMemberQuizStatRepository.findByUserId(USER_ID))
                .thenReturn(List.of(stat(8, 10), stat(6, 10)));

        MemberStat result = provider.getStatForUser(USER_ID);

        assertEquals(USER_ID, result.userId());
        assertEquals(14, result.totalScore());
        assertEquals(2, result.quizCount());
        assertEquals(0.7, result.accuracy());
    }

    @Test
    void getStatForUser_shouldReturnZeroStat_whenUserHasNoQuizStats() {
        QuizAttemptMemberStatProvider provider = new QuizAttemptMemberStatProvider(clanMemberQuizStatRepository);

        when(clanMemberQuizStatRepository.findByUserId(USER_ID)).thenReturn(List.of());

        MemberStat result = provider.getStatForUser(USER_ID);

        assertEquals(USER_ID, result.userId());
        assertEquals(0, result.totalScore());
        assertEquals(0, result.quizCount());
        assertEquals(0.0, result.accuracy());
    }

    private ClanMemberQuizStat stat(int score, int total) {
        ClanMemberQuizStat stat = new ClanMemberQuizStat();
        stat.setUserId(USER_ID);
        stat.setReadingId(UUID.randomUUID());
        stat.setScore(score);
        stat.setTotal(total);
        stat.setCompletedAt(LocalDateTime.now());
        return stat;
    }
}
