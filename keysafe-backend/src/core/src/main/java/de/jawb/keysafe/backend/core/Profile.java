package de.jawb.keysafe.backend.core;

import java.time.LocalDateTime;

public record Profile(
        ProfileId id,
        String profileName,
        String data,
        String dataChecksum,
        LocalDateTime dataDate) {
    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", profileName='" + profileName + '\'' +
                ", data='...'"  +
                ", dataChecksum='" + dataChecksum + '\'' +
                ", dataDate=" + dataDate +
                '}';
    }
}
