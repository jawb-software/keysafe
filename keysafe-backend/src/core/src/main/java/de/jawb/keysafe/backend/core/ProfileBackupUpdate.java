package de.jawb.keysafe.backend.core;

import java.time.LocalDateTime;

public record ProfileBackupUpdate(
        ProfileId id,
        String profileName,
        String data,
        String dataChecksum,
        LocalDateTime dataDate) {

    @Override
    public String toString() {
        return "ProfileBackupUpdate{" +
                "id=" + id +
                ", profileName='" + profileName + '\'' +
                ", data='..." +
                ", dataChecksum='" + dataChecksum + '\'' +
                ", dataDate=" + dataDate +
                '}';
    }
}
