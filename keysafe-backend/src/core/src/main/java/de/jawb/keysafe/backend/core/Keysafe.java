package de.jawb.keysafe.backend.core;

import java.time.LocalDateTime;
import java.util.UUID;

public record Keysafe(UUID id, String name, String accessUsername, String accessPassword, LocalDateTime createdDate) {
}
