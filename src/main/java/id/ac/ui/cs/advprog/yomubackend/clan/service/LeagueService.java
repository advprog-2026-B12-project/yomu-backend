package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.league.ClanScoreProvider;
import id.ac.ui.cs.advprog.yomubackend.clan.league.ClanScoreProviderResolver;
import id.ac.ui.cs.advprog.yomubackend.clan.league.MemberStat;
import id.ac.ui.cs.advprog.yomubackend.clan.league.MemberStatProvider;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class LeagueService {

    private static final int MAX_MOVE_PER_DIVISION = 5;

    private final ClanRepository clanRepository;
    private final ClanMemberRepository clanMemberRepository;
    private final MemberStatProvider memberStatProvider;
    private final ClanScoreProviderResolver resolver;

    public LeagueService(ClanRepository clanRepository,
                         ClanMemberRepository clanMemberRepository,
                         MemberStatProvider memberStatProvider,
                         ClanScoreProviderResolver resolver) {
        this.clanRepository = clanRepository;
        this.clanMemberRepository = clanMemberRepository;
        this.memberStatProvider = memberStatProvider;
        this.resolver = resolver;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getLeaderboardByDivision(String division) {
        List<Clan> clans = clanRepository.findByDivision(division.toUpperCase());
        List<LeaderboardEntryResponse> leaderboard = new ArrayList<>();

        for (Clan clan : clans) {
            List<ClanMember> members = clanMemberRepository.findByClan(clan);

            List<MemberStat> stats = members.stream()
                    .map(member -> memberStatProvider.getStatForUser(member.getUserId()))
                    .toList();

            ClanScoreProvider provider = resolver.resolve(clan.getDivision());
            int score = (int) Math.round(provider.calculateScore(stats));

            leaderboard.add(new LeaderboardEntryResponse(
                    0,
                    clan.getId(),
                    clan.getName(),
                    clan.getDivision(),
                    members.size(),
                    score
            ));
        }

        leaderboard.sort(Comparator.comparingInt(LeaderboardEntryResponse::getScore).reversed());

        List<LeaderboardEntryResponse> ranked = new ArrayList<>();
        for (int i = 0; i < leaderboard.size(); i++) {
            LeaderboardEntryResponse row = leaderboard.get(i);

            ranked.add(new LeaderboardEntryResponse(
                    i + 1,
                    row.getClanId(),
                    row.getClanName(),
                    row.getDivision(),
                    row.getMemberCount(),
                    row.getScore()
            ));
        }

        return ranked;
    }

    @Transactional
    public void triggerSeasonReset() {
        processDivision("BRONZE", "SILVER", null);
        processDivision("SILVER", "GOLD", "BRONZE");
        processDivision("GOLD", "DIAMOND", "SILVER");
        processDivision("DIAMOND", null, "GOLD");
    }

    private void processDivision(String currentDivision,
                                 String promotionTarget,
                                 String relegationTarget) {
        List<Clan> rankedClans = getRankedClans(currentDivision);
        int moveCount = calculateMoveCount(rankedClans.size());

        if (moveCount == 0) {
            return;
        }

        promoteTopClans(rankedClans, moveCount, promotionTarget);
        relegateBottomClans(rankedClans, moveCount, relegationTarget);

        clanRepository.saveAll(rankedClans);
    }

    private List<Clan> getRankedClans(String division) {
        List<Clan> clans = clanRepository.findByDivision(division);

        return clans.stream()
                .sorted(Comparator.comparingInt(this::calculateClanScore).reversed())
                .toList();
    }

    private int calculateMoveCount(int totalClans) {
        return Math.min(MAX_MOVE_PER_DIVISION, totalClans / 2);
    }

    private void promoteTopClans(List<Clan> rankedClans, int moveCount, String targetDivision) {
        if (targetDivision == null) {
            return;
        }

        for (int i = 0; i < moveCount; i++) {
            rankedClans.get(i).setDivision(targetDivision);
        }
    }

    private void relegateBottomClans(List<Clan> rankedClans, int moveCount, String targetDivision) {
        if (targetDivision == null) {
            return;
        }

        for (int i = rankedClans.size() - moveCount; i < rankedClans.size(); i++) {
            rankedClans.get(i).setDivision(targetDivision);
        }
    }

    private int calculateClanScore(Clan clan) {
        List<ClanMember> members = clanMemberRepository.findByClan(clan);

        List<MemberStat> stats = members.stream()
                .map(member -> memberStatProvider.getStatForUser(member.getUserId()))
                .toList();

        ClanScoreProvider provider = resolver.resolve(clan.getDivision());
        return (int) Math.round(provider.calculateScore(stats));
    }
}