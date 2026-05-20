package id.ac.ui.cs.advprog.yomubackend.clan.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.ac.ui.cs.advprog.yomubackend.clan.completion.ClanPromotion;
import id.ac.ui.cs.advprog.yomubackend.clan.completion.ClanPromotionProcessor;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.clan.exception.UserNotInClanException;
import id.ac.ui.cs.advprog.yomubackend.clan.league.ClanScoreProvider;
import id.ac.ui.cs.advprog.yomubackend.clan.league.ClanScoreProviderResolver;
import id.ac.ui.cs.advprog.yomubackend.clan.league.LeagueDivision;
import id.ac.ui.cs.advprog.yomubackend.clan.league.MemberStat;
import id.ac.ui.cs.advprog.yomubackend.clan.league.MemberStatProvider;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;

@Service
public class LeagueServiceImpl implements LeagueService {

    private static final int MAX_MOVE_PER_DIVISION = 5;

    private final ClanRepository clanRepository;
    private final ClanMemberRepository clanMemberRepository;
    private final MemberStatProvider memberStatProvider;
    private final ClanScoreProviderResolver resolver;
    private final ClanScoreMultiplierCalculator multiplierCalculator;
    private final ClanPromotionProcessor clanPromotionProcessor;

    public LeagueServiceImpl(ClanRepository clanRepository,
                             ClanMemberRepository clanMemberRepository,
                             MemberStatProvider memberStatProvider,
                             ClanScoreProviderResolver resolver,
                             ClanScoreMultiplierCalculator multiplierCalculator,
                             ClanPromotionProcessor clanPromotionProcessor) {
        this.clanRepository = clanRepository;
        this.clanMemberRepository = clanMemberRepository;
        this.memberStatProvider = memberStatProvider;
        this.resolver = resolver;
        this.multiplierCalculator = multiplierCalculator;
        this.clanPromotionProcessor = clanPromotionProcessor;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getLeaderboardByDivision(String division) {
        LeagueDivision leagueDivision = LeagueDivision.from(division);
        List<Clan> clans = clanRepository.findByDivision(leagueDivision.value());
        if (clans.isEmpty()) {
            return List.of();
        }

        Map<Long, List<ClanMember>> membersByClanId = clanMemberRepository.findByClanIn(clans)
                .stream()
                .collect(Collectors.groupingBy(m -> m.getClan().getId()));

        List<LeaderboardEntryResponse> leaderboard = new ArrayList<>();
        for (Clan clan : clans) {
            List<ClanMember> members = membersByClanId.getOrDefault(clan.getId(), List.of());
            int score = calculateClanScore(clan, members);
            double multiplier = multiplierCalculator.calculateMultiplier(members);
            List<String> activeModifiers = multiplierCalculator.getActiveModifierNames(members);
            leaderboard.add(new LeaderboardEntryResponse(
                    0, clan.getId(), clan.getName(), clan.getDivision(), members.size(),
                    score, multiplier, activeModifiers));
        }

        leaderboard.sort(Comparator.comparingInt(LeaderboardEntryResponse::getScore).reversed());
        return rankLeaderboard(leaderboard);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getLeaderboardForUser(UUID userId) {
        ClanMember member = clanMemberRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotInClanException("User is not in any clan"));

        return getLeaderboardByDivision(member.getClan().getDivision());
    }

    @Override
    @Transactional
    public void triggerSeasonReset() {
        Map<LeagueDivision, List<Clan>> clansByDivision = findClansByDivision();
        Map<Long, LeagueDivision> originalDivisionByClanId = new HashMap<>();
        Map<Long, LeagueDivision> targetDivisionByClanId = new HashMap<>();

        for (LeagueDivision division : LeagueDivision.ordered()) {
            List<Clan> clans = clansByDivision.get(division);
            clans.forEach(clan -> originalDivisionByClanId.put(clan.getId(), division));
            planDivisionMoves(clans, division, targetDivisionByClanId);
        }

        List<Clan> allClans = clansByDivision.values().stream()
                .flatMap(List::stream)
                .toList();
        for (Clan clan : allClans) {
            LeagueDivision targetDivision = targetDivisionByClanId.get(clan.getId());
            if (targetDivision != null) {
                clan.setDivision(targetDivision.value());
            }
        }

        clanRepository.saveAll(allClans);

        LocalDateTime now = LocalDateTime.now();
        allClans.forEach(clan -> {
            LeagueDivision target = targetDivisionByClanId.get(clan.getId());
            LeagueDivision original = originalDivisionByClanId.get(clan.getId());
            if (target != null && isPromotion(original, target)) {
                List<UUID> memberIds = clanMemberRepository.findByClan(clan).stream()
                        .map(ClanMember::getUserId)
                        .toList();
                clanPromotionProcessor.processPromotion(
                        new ClanPromotion(clan.getId(), memberIds, target.value(), now));
            }
        });
    }

    private boolean isPromotion(LeagueDivision from, LeagueDivision to) {
        return to.ordinal() > from.ordinal();
    }

    private List<LeaderboardEntryResponse> rankLeaderboard(List<LeaderboardEntryResponse> leaderboard) {
        List<LeaderboardEntryResponse> ranked = new ArrayList<>();

        for (int i = 0; i < leaderboard.size(); i++) {
            LeaderboardEntryResponse row = leaderboard.get(i);
            ranked.add(new LeaderboardEntryResponse(
                    i + 1,
                    row.getClanId(),
                    row.getClanName(),
                    row.getDivision(),
                    row.getMemberCount(),
                    row.getScore(),
                    row.getScoreMultiplier(),
                    row.getActiveModifiers()
            ));
        }

        return ranked;
    }

    private Map<LeagueDivision, List<Clan>> findClansByDivision() {
        Map<LeagueDivision, List<Clan>> clansByDivision = new EnumMap<>(LeagueDivision.class);

        for (LeagueDivision division : LeagueDivision.ordered()) {
            clansByDivision.put(division, clanRepository.findByDivision(division.value()));
        }

        return clansByDivision;
    }

    private void planDivisionMoves(
            List<Clan> clans,
            LeagueDivision currentDivision,
            Map<Long, LeagueDivision> targetDivisionByClanId) {

        int moveCount = calculateMoveCount(clans.size());
        if (moveCount == 0) {
            return;
        }

        Map<Long, List<ClanMember>> membersByClanId = clanMemberRepository.findByClanIn(clans)
                .stream()
                .collect(Collectors.groupingBy(m -> m.getClan().getId()));

        Map<Long, Integer> scoresByClanId = new HashMap<>();
        for (Clan clan : clans) {
            List<ClanMember> members = membersByClanId.getOrDefault(clan.getId(), List.of());
            scoresByClanId.put(clan.getId(), calculateClanScore(clan, members));
        }

        List<Clan> rankedClans = clans.stream()
                .sorted(Comparator.comparingInt((Clan clan) -> scoresByClanId.get(clan.getId()))
                        .reversed()
                        .thenComparing(Clan::getId))
                .toList();

        currentDivision.promotionTarget().ifPresent(promoTarget -> {
            for (int i = 0; i < moveCount; i++) {
                targetDivisionByClanId.put(rankedClans.get(i).getId(), promoTarget);
            }
        });

        currentDivision.relegationTarget().ifPresent(releTarget -> {
            for (int i = rankedClans.size() - moveCount; i < rankedClans.size(); i++) {
                targetDivisionByClanId.put(rankedClans.get(i).getId(), releTarget);
            }
        });
    }

    private int calculateMoveCount(int totalClans) {
        return Math.min(MAX_MOVE_PER_DIVISION, totalClans / 2);
    }

    private int calculateClanScore(Clan clan, List<ClanMember> members) {
        List<MemberStat> stats = members.stream()
                .map(member -> memberStatProvider.getStatForUser(member.getUserId()))
                .toList();

        ClanScoreProvider provider = resolver.resolve(clan.getDivision());
        double baseScore = provider.calculateScore(stats);
        double multiplier = multiplierCalculator.calculateMultiplier(members);
        return (int) Math.round(baseScore * multiplier);
    }
}
