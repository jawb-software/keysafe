package de.jawb.keysafe.backend.core;

import java.time.LocalDateTime;
import java.util.UUID;

public record NewProfileBackup(
        UUID keysafeId,
        String profileName,
        String data,
        String dataChecksum,
        LocalDateTime dataDate) {
    @Override
    public String toString() {
        return "NewProfileBackup{" +
                "keysafeId=" + keysafeId +
                ", profileName='" + profileName + '\'' +
                ", data='...'"  +
                ", dataChecksum='" + dataChecksum + '\'' +
                ", dataDate=" + dataDate +
                '}';
    }
}
