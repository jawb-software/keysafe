package de.jawb.keysafe.backend.core.service;

import de.jawb.keysafe.backend.core.*;

import java.util.List;
import java.util.UUID;

public interface KeysafeService {

	Profile getById(ProfileId id);

    void updateProfile(ProfileBackupUpdate backup);

    UUID backupProfile(NewProfileBackup backup);

    UUID register(KeysafeSignup userSignUp);

    List<Profile> getProfiles(UUID keysafeId);

    Profile getProfileRevision(ProfileRevisionId revisionId);
    List<ProfileRevision> getRevisions(ProfileId id);
}
