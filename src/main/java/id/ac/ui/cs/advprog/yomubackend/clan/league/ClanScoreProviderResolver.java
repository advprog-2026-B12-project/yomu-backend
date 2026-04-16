package id.ac.ui.cs.advprog.yomubackend.clan.league;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ClanScoreProviderResolver {

    private final Map<String, ClanScoreProvider> providerMap;

    public ClanScoreProviderResolver(List<ClanScoreProvider> providers) {
        this.providerMap = providers.stream()
                .collect(Collectors.toMap(
                        p -> p.getDivision().toUpperCase(),
                        Function.identity()
                ));
    }

    public ClanScoreProvider resolve(String division) {
        ClanScoreProvider provider = providerMap.get(division.toUpperCase());

        if (provider == null) {
            throw new IllegalArgumentException("Unknown division: " + division);
        }

        return provider;
    }
}