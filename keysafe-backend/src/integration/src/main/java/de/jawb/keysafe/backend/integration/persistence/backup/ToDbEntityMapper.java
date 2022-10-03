package de.jawb.keysafe.backend.integration.persistence.backup;

import de.jawb.keysafe.backend.core.KeysafeSignup;
import de.jawb.keysafe.backend.core.NewProfileBackup;
import de.jawb.keysafe.backend.core.ProfileBackupUpdate;

import java.time.LocalDateTime;

public class ToDbEntityMapper {

    public static DbKeysafeEntity keysafeEntityFromCore(KeysafeSignup keysafe){

        DbKeysafeEntity entity = new DbKeysafeEntity();
        entity.setSafeName(keysafe.name());
        entity.setAccessUserName(keysafe.accessUserName());
        entity.setAccessPassword(keysafe.accessPassword());
        entity.setCreated(LocalDateTime.now());

        return entity;
    }

    public static DbProfileBackupEntity profileEntityFromCore(NewProfileBackup profile){

        DbProfileBackupEntity e = new DbProfileBackupEntity();
        e.setSafeId(profile.keysafeId());
        e.setProfileName(profile.profileName());
        e.setData(profile.data());
        e.setDataChecksum(profile.dataChecksum());
        e.setDataDate(profile.dataDate());

        return e;
    }

    public static void applyChanges(DbProfileBackupEntity e, ProfileBackupUpdate profile){
        e.setProfileName(profile.profileName());
        e.setData(profile.data());
        e.setDataChecksum(profile.dataChecksum());
        e.setDataDate(profile.dataDate());
    }

}
