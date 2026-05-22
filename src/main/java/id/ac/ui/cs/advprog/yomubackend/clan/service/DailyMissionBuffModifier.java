package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberDailyMissionCompletionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class DailyMissionBuffModifier implements ClanScoreModifier {

    private static final double DAILY_MISSION_COMPLETION_THRESHOLD = 0.5;
    private static final double DAILY_MISSION_BUFF_MULTIPLIER = 1.2;
    private static final double NEUTRAL_MULTIPLIER = 1.0;

    private final ClanMemberDailyMissionCompletionRepository completionRepository;
    private final Clock clock;

    @Autowired
    public DailyMissionBuffModifier(ClanMemberDailyMissionCompletionRepository completionRepository) {
        this(completionRepository, Clock.systemDefaultZone());
    }

    DailyMissionBuffModifier(ClanMemberDailyMissionCompletionRepository completionRepository,
                             Clock clock) {
        this.completionRepository = completionRepository;
        this.clock = clock;
    }

    @Override
    public String getModifierName() {
        return "Productivity Buff";
    }

    @Override
    public double calculateMultiplier(List<ClanMember> members) {
        if (members == null || members.isEmpty()) {
            return NEUTRAL_MULTIPLIER;
        }

        LocalDate today = LocalDate.now(clock);
        List<UUID> memberIds = members.stream().map(ClanMember::getUserId).toList();
        long completedCount = completionRepository.countByUserIdInAndDateAssigned(memberIds, today);

        double completionRate = (double) completedCount / members.size();
        return completionRate >= DAILY_MISSION_COMPLETION_THRESHOLD
                ? DAILY_MISSION_BUFF_MULTIPLIER
                : NEUTRAL_MULTIPLIER;
    }
}
