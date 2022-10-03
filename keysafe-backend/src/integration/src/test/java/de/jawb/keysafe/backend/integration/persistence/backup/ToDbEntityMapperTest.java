package de.jawb.keysafe.backend.integration.persistence.backup;

import de.jawb.keysafe.backend.core.KeysafeSignup;
import de.jawb.keysafe.backend.core.NewProfileBackup;
import de.jawb.keysafe.backend.core.ProfileBackupUpdate;
import de.jawb.keysafe.backend.core.ProfileId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

class ToDbEntityMapperTest {

    @Test
    void keysafeFromCore() {

        KeysafeSignup signup = new KeysafeSignup(
                "Test-profile",
                "keysafe",
                "password"
        );

        DbKeysafeEntity e = ToDbEntityMapper.keysafeEntityFromCore(signup);

        Assertions.assertEquals(signup.name(), e.getSafeName());
        Assertions.assertEquals(signup.accessUserName(), e.getAccessUserName());
        Assertions.assertEquals(signup.accessPassword(), e.getAccessPassword());
    }

    @Test
    void profileEntityFromCore() {

        NewProfileBackup profile = new NewProfileBackup(
                UUID.randomUUID(),
                "Test-profile",
                "data",
                "checksum",
                LocalDateTime.now()
        );

        DbProfileBackupEntity e = ToDbEntityMapper.profileEntityFromCore(profile);

        Assertions.assertEquals(profile.keysafeId(), e.getSafeId());
        Assertions.assertEquals(profile.profileName(), e.getProfileName());
        Assertions.assertEquals(profile.data(), e.getData());
        Assertions.assertEquals(profile.dataChecksum(), e.getDataChecksum());
        Assertions.assertEquals(profile.dataDate(), e.getDataDate());

    }

    @Test
    void applyChanges() {

        DbProfileBackupEntity e = new DbProfileBackupEntity();
        e.setId(null);
        e.setSafeId(null);
        e.setProfileName("profile-name");
        e.setData("data-old");
        e.setData("checksum-old");
        e.setDataDate(LocalDateTime.now().minusDays(1));

        ProfileBackupUpdate update = new ProfileBackupUpdate(
                new ProfileId(UUID.randomUUID(), UUID.randomUUID()),
                "Test-profile",
                "data",
                "checksum",
                LocalDateTime.now()
        );

        ToDbEntityMapper.applyChanges(e, update);

        Assertions.assertNull(e.getId());
        Assertions.assertNull(e.getSafeId());

        Assertions.assertEquals(update.profileName(), e.getProfileName());
        Assertions.assertEquals(update.data(), e.getData());
        Assertions.assertEquals(update.dataChecksum(), e.getDataChecksum());
        Assertions.assertEquals(update.dataDate(), e.getDataDate());

    }
}