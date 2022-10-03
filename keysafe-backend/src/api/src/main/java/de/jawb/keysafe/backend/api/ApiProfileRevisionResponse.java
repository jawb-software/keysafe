package de.jawb.keysafe.backend.api;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiProfileRevisionResponse(
        UUID revisionId,
        String profileName,
        String dataChecksum,
        LocalDateTime dataDate) {

}
