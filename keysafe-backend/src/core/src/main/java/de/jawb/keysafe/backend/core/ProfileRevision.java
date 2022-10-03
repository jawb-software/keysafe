package de.jawb.keysafe.backend.core;

import java.time.LocalDateTime;

public record ProfileRevision(
        ProfileRevisionId id,
        String profileName,
        String dataChecksum,
        LocalDateTime dataDate) {
}
