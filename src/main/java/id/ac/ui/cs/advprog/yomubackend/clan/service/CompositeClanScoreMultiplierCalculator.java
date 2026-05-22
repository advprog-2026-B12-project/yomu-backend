package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberDailyMissionCompletionRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberQuizStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

@Service
public class CompositeClanScoreMultiplierCalculator implements ClanScoreMultiplierCalculator {

    private static final double NEUTRAL_MULTIPLIER = 1.0;

    private final List<ClanScoreModifier> modifiers;

    @Autowired
    public CompositeClanScoreMultiplierCalculator(List<ClanScoreModifier> modifiers) {
        this.modifiers = List.copyOf(modifiers);
    }

    CompositeClanScoreMultiplierCalculator(ClanMemberDailyMissionCompletionRepository completionRepository,
                                           ClanMemberQuizStatRepository quizStatRepository,
                                           Clock clock) {
        this(List.of(
                new DailyMissionBuffModifier(completionRepository, clock),
                new LowAccuracyDebuffModifier(quizStatRepository, clock)
        ));
    }

    @Override
    public double calculateMultiplier(List<ClanMember> members) {
        if (members == null || members.isEmpty()) {
            return NEUTRAL_MULTIPLIER;
        }

        return modifiers.stream()
                .mapToDouble(modifier -> modifier.calculateMultiplier(members))
                .reduce(NEUTRAL_MULTIPLIER, (left, right) -> left * right);
    }

    @Override
    public List<String> getActiveModifierNames(List<ClanMember> members) {
        if (members == null || members.isEmpty()) {
            return List.of();
        }

        return modifiers.stream()
                .filter(modifier -> modifier.calculateMultiplier(members) != NEUTRAL_MULTIPLIER)
                .map(ClanScoreModifier::getModifierName)
                .toList();
    }
}
