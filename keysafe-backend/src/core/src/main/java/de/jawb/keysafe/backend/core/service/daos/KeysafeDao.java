package de.jawb.keysafe.backend.core.service.daos;

import de.jawb.keysafe.backend.core.*;
import de.jawb.keysafe.backend.core.Keysafe;

import java.util.List;
import java.util.UUID;

public interface KeysafeDao {

    boolean checkDb();

    AccessData findByAccessUsername(String accessUsername);

    Profile getProfile(ProfileId profileId);

    UUID create(KeysafeSignup keysafe);
    UUID create(NewProfileBackup backup);

    boolean keysafeExists(UUID keysafeId);

    void update(ProfileBackupUpdate profile);

    List<Profile> findAllProfiles(UUID keysafeId);

    List<ProfileRevision> getRevisions(ProfileId profileId);
    Profile getRevision(ProfileRevisionId id);
    void createRevision(ProfileId profileId);
}
