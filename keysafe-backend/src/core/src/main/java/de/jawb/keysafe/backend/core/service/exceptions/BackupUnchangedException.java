package de.jawb.keysafe.backend.core.service.exceptions;

import de.jawb.keysafe.backend.core.base.AppException;

public class BackupUnchangedException extends AppException {
    public BackupUnchangedException() {
        super("backup contains no change");
    }
}
