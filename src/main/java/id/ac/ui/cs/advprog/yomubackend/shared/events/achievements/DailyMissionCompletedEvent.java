package id.ac.ui.cs.advprog.yomubackend.shared.events.achievements;

import java.time.LocalDate;
import java.util.UUID;

public record DailyMissionCompletedEvent(UUID userId, LocalDate date) {
}
