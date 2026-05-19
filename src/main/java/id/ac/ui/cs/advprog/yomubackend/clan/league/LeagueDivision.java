package id.ac.ui.cs.advprog.yomubackend.clan.league;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public enum LeagueDivision {
    BRONZE,
    SILVER,
    GOLD,
    DIAMOND;

    public static LeagueDivision from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Division is required");
        }

        try {
            return LeagueDivision.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown division: " + value, exception);
        }
    }

    public static List<LeagueDivision> ordered() {
        return List.of(BRONZE, SILVER, GOLD, DIAMOND);
    }

    public Optional<LeagueDivision> promotionTarget() {
        return switch (this) {
            case BRONZE -> Optional.of(SILVER);
            case SILVER -> Optional.of(GOLD);
            case GOLD -> Optional.of(DIAMOND);
            case DIAMOND -> Optional.empty();
        };
    }

    public Optional<LeagueDivision> relegationTarget() {
        return switch (this) {
            case BRONZE -> Optional.empty();
            case SILVER -> Optional.of(BRONZE);
            case GOLD -> Optional.of(SILVER);
            case DIAMOND -> Optional.of(GOLD);
        };
    }

    public String value() {
        return name();
    }
}
