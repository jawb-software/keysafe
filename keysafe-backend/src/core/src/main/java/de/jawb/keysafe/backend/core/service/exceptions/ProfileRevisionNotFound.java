package de.jawb.keysafe.backend.core.service.exceptions;

import de.jawb.keysafe.backend.core.base.DataNotFoundException;

import java.util.UUID;

public class ProfileRevisionNotFound extends DataNotFoundException {
    public ProfileRevisionNotFound(UUID id) {
        super("profile revision not found: " + id);
    }
}
