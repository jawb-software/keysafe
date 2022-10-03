package de.jawb.keysafe.backend.core.service.exceptions;

import de.jawb.keysafe.backend.core.ProfileId;
import de.jawb.keysafe.backend.core.base.DataNotFoundException;

public class ProfileNotFoundException extends DataNotFoundException {
    public ProfileNotFoundException(ProfileId id) {
        super("profile not found: " + id.profileId());
    }
}
