package de.jawb.keysafe.backend.api;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiProfileInfoResponse(
        UUID profileId,
        String profileName,
        String dataChecksum,
        LocalDateTime dataDate) {

}
