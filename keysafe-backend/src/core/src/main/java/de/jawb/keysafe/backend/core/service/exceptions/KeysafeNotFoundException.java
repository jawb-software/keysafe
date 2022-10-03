package de.jawb.keysafe.backend.core.service.exceptions;

import de.jawb.keysafe.backend.core.base.DataNotFoundException;

import java.util.UUID;

public class KeysafeNotFoundException extends DataNotFoundException {
    public KeysafeNotFoundException(UUID keysafeId) {
        super("keysafe not found: " + keysafeId);
    }
}
