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
import java.util.HashMap;
import java.util.Map;


@Service
public class LeagueService {

    private static final int MAX_MOVE_PER_DIVISION = 5;

    private final ClanRepository clanRepository;
    private final ClanMemberRepository clanMemberRepository;
    private final MemberStatProvider memberStatProvider;
    private final ClanScoreProviderResolver resolver;
    private final ClanScoreModifierService clanScoreModifierService;

    public LeagueService(ClanRepository clanRepository,
                         ClanMemberRepository clanMemberRepository,
                         MemberStatProvider memberStatProvider,
                         ClanScoreProviderResolver resolver,
                         ClanScoreModifierService clanScoreModifierService) {
        this.clanRepository = clanRepository;
        this.clanMemberRepository = clanMemberRepository;
        this.memberStatProvider = memberStatProvider;
        this.resolver = resolver;
        this.clanScoreModifierService = clanScoreModifierService;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getLeaderboardByDivision(String division) {
        List<Clan> clans = clanRepository.findByDivision(division.toUpperCase());
        List<LeaderboardEntryResponse> leaderboard = new ArrayList<>();

        for (Clan clan : clans) {
            List<ClanMember> members = clanMemberRepository.findByClan(clan);

            int score = calculateClanScore(clan, members);

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
        Map<Long, String> targetDivisionByClanId = new HashMap<>();


        List<Clan> bronzeClans = clanRepository.findByDivision("BRONZE");
        List<Clan> silverClans = clanRepository.findByDivision("SILVER");
        List<Clan> goldClans = clanRepository.findByDivision("GOLD");
        List<Clan> diamondClans = clanRepository.findByDivision("DIAMOND");

        planDivisionMoves(bronzeClans, "SILVER", null, targetDivisionByClanId);
        planDivisionMoves(silverClans, "GOLD", "BRONZE", targetDivisionByClanId);
        planDivisionMoves(goldClans, "DIAMOND", "SILVER", targetDivisionByClanId);
        planDivisionMoves(diamondClans, null, "GOLD", targetDivisionByClanId);

        List<Clan> allClans = new ArrayList<>();
        allClans.addAll(bronzeClans);
        allClans.addAll(silverClans);
        allClans.addAll(goldClans);
        allClans.addAll(diamondClans);

        for(Clan clan: allClans){
            String targetDivision = targetDivisionByClanId.get(clan.getId());

            if(targetDivision != null)
                clan.setDivision(targetDivision);
        }

        clanRepository.saveAll(allClans);
    }

    private void planDivisionMoves(
        List<Clan> clans, 
        String promoTarget, 
        String releTarget, 
        Map<Long, String> targetDivisionByClanId) {
        
        List<Clan> rankedClans = clans.stream()
                .sorted(Comparator.comparingInt((Clan clan) -> calculateClanScore(clan))
                        .reversed()
                        .thenComparing(Clan::getId))
                .toList();
        int moveCount = calculateMoveCount(clans.size());

        if(moveCount == 0)
            return;

        if(promoTarget != null){
            for(int i = 0; i < moveCount; i++){
                targetDivisionByClanId.put(rankedClans.get(i).getId(), promoTarget);
            }
        }

        if(releTarget != null){
            for(int i = rankedClans.size() - moveCount; i < rankedClans.size(); i++) {
                targetDivisionByClanId.put(rankedClans.get(i).getId(), releTarget);
            }
        }
    }

    private int calculateMoveCount(int totalClans) {
        return Math.min(MAX_MOVE_PER_DIVISION, totalClans / 2);
    }

    private int calculateClanScore(Clan clan) {
        List<ClanMember> members = clanMemberRepository.findByClan(clan);
        return calculateClanScore(clan, members);
    }

    private int calculateClanScore(Clan clan, List<ClanMember> members) {
        List<MemberStat> stats = members.stream()
                .map(member -> memberStatProvider.getStatForUser(member.getUserId()))
                .toList();

        ClanScoreProvider provider = resolver.resolve(clan.getDivision());
        double baseScore = provider.calculateScore(stats);
        double multiplier = clanScoreModifierService.calculateMultiplier(members);
        return (int) Math.round(baseScore * multiplier);
    }
}
