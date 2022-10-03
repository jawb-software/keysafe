package de.jawb.keysafe.backend.api;

import java.time.LocalDateTime;

public record ApiProfileResponse(
        String profileName,
        String data,
        String dataChecksum,
        LocalDateTime dataDate) {

}