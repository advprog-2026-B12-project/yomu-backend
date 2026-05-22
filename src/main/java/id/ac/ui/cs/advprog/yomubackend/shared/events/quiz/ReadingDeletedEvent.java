package id.ac.ui.cs.advprog.yomubackend.shared.events.quiz;

import java.util.UUID;
import java.time.LocalDateTime;

public record ReadingDeletedEvent(UUID readingId, LocalDateTime deletedAt) {
}