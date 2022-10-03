package de.jawb.keysafe.backend.core.service.exceptions;

import de.jawb.keysafe.backend.core.base.DataNotFoundException;

public class KeysafeServiceNotReadyException extends DataNotFoundException {
    public KeysafeServiceNotReadyException(String service) {
        super("service not ready: " + service);
    }
}
