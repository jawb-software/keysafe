package de.jawb.keysafe.backend.core.service;

import de.jawb.keysafe.backend.core.*;
import de.jawb.keysafe.backend.core.service.daos.KeysafeDao;
import de.jawb.keysafe.backend.core.service.exceptions.BackupUnchangedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service
public class KeysafeServiceImpl implements KeysafeService {

    private static final Logger logger = LoggerFactory.getLogger(KeysafeServiceImpl.class);

    @Inject
    private KeysafeDao keysafeDao;

    @Override
    public UUID register(KeysafeSignup signup) {
        logger.info("register: {}", signup);

        return keysafeDao.create(signup);
    }

    @Override
    public Profile getById(ProfileId id) {
        logger.info("getById: {}", id);

        return keysafeDao.getProfile(id);
    }

    @Override
    public void updateProfile(ProfileBackupUpdate backup) {
        logger.info("updateProfile: {}", backup);

        Profile currentState = keysafeDao.getProfile(backup.id());

        if(currentState.dataChecksum().equals(backup.dataChecksum())){
            logger.warn("update skipped. no changes available.");
            throw new BackupUnchangedException();
        }

        keysafeDao.createRevision(backup.id());
        keysafeDao.update(backup);
    }

    @Override
    public UUID backupProfile(NewProfileBackup backup) {
        logger.info("backupProfile: {}", backup);

        return keysafeDao.create(backup);
    }

    @Override
    public List<ProfileRevision> getRevisions(ProfileId profileId) {
        logger.info("getRevisions: {}", profileId);

        return keysafeDao.getRevisions(profileId);
    }

    @Override
    public List<Profile> getProfiles(UUID keysafeId) {
        logger.info("getProfiles: {}", keysafeId);

        return keysafeDao.findAllProfiles(keysafeId);
    }

    @Override
    public Profile getProfileRevision(ProfileRevisionId revisionId) {
        logger.info("getProfileRevision: {}", revisionId);

        return keysafeDao.getRevision(revisionId);
    }
}
