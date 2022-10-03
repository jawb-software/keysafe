package de.jawb.keysafe.backend.integration.persistence.backup;

import de.jawb.keysafe.backend.core.AccessData;
import de.jawb.keysafe.backend.core.Profile;
import de.jawb.keysafe.backend.core.ProfileRevision;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

class ToCoreMapperTest {

    @Test
    void keysafeFromDbEntity() {

        DbKeysafeEntity e = new DbKeysafeEntity();
        e.setId(UUID.randomUUID());
        e.setAccessUserName("keysafe");
        e.setAccessPassword("password");
        e.setSafeName("My keysafe");
        e.setCreated(LocalDateTime.now());

        AccessData accessData = ToCoreMapper.accessDataFromDbEntity(e);

        Assertions.assertEquals(e.getAccessUserName(), accessData.userName());
        Assertions.assertEquals(e.getAccessPassword(), accessData.encryptedPassword());

    }

    @Test
    void profileBackupFromDbEntity() {

        DbProfileBackupEntity e = new DbProfileBackupEntity();
        e.setId(UUID.randomUUID());
        e.setSafeId(UUID.randomUUID());
        e.setProfileName("Test-profile");
        e.setData("data");
        e.setDataChecksum("checksum");
        e.setDataDate(LocalDateTime.now());

        Profile profile = ToCoreMapper.profileBackupFromDbEntity(e);

        Assertions.assertEquals(e.getId(), profile.id().profileId());
        Assertions.assertEquals(e.getSafeId(), profile.id().keysafeId());
        Assertions.assertEquals(e.getProfileName(), profile.profileName());
        Assertions.assertEquals(e.getData(), profile.data());
        Assertions.assertEquals(e.getDataChecksum(), profile.dataChecksum());
        Assertions.assertEquals(e.getDataDate(), profile.dataDate());

        {
            List<Profile> profiles = ToCoreMapper.profileBackupsFromDbEntities(List.of(e));
            Assertions.assertEquals(1, profiles.size());
        }

    }

    @Test
    void profileRevisionFromDbEntity() {

        DbProfileRevisionEntity e = new DbProfileRevisionEntity();
        e.setId(UUID.randomUUID());
        e.setProfileId(UUID.randomUUID());
        e.setSafeId(UUID.randomUUID());
        e.setProfileName("Test-profile");
        e.setData("data");
        e.setDataChecksum("checksum");
        e.setDataDate(LocalDateTime.now());

        ProfileRevision revision = ToCoreMapper.profileRevisionFromDbEntity(e);

        Assertions.assertEquals(e.getId(), revision.id().revisionId());
        Assertions.assertEquals(e.getProfileId(), revision.id().profileId());
        Assertions.assertEquals(e.getSafeId(), revision.id().keysafeId());
        Assertions.assertEquals(e.getProfileName(), revision.profileName());
        Assertions.assertEquals(e.getDataChecksum(), revision.dataChecksum());
        Assertions.assertEquals(e.getDataDate(), revision.dataDate());

    }

    @Test
    void revisionEntryFromDbEntity() {

        DbProfileRevisionEntity e = new DbProfileRevisionEntity();
        e.setId(UUID.randomUUID());
        e.setProfileId(UUID.randomUUID());
        e.setSafeId(UUID.randomUUID());
        e.setProfileName("Test-profile");
        e.setData("data");
        e.setDataChecksum("checksum");
        e.setDataDate(LocalDateTime.now());

        Profile profile = ToCoreMapper.revisionEntryFromDbEntity(e);

        Assertions.assertEquals(e.getProfileId(), profile.id().profileId());
        Assertions.assertEquals(e.getSafeId(), profile.id().keysafeId());
        Assertions.assertEquals(e.getProfileName(), profile.profileName());
        Assertions.assertEquals(e.getData(), profile.data());
        Assertions.assertEquals(e.getDataChecksum(), profile.dataChecksum());
        Assertions.assertEquals(e.getDataDate(), profile.dataDate());

        {
            List<ProfileRevision> revisions = ToCoreMapper.revisionEntriesFromDbEntities(List.of(e));
            Assertions.assertEquals(1, revisions.size());
        }

    }

}