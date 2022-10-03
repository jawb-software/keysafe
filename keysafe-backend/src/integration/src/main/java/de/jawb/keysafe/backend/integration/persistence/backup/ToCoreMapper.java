package de.jawb.keysafe.backend.integration.persistence.backup;

import de.jawb.keysafe.backend.core.*;

import java.util.List;
import java.util.stream.Collectors;

public class ToCoreMapper {

    public static AccessData accessDataFromDbEntity(DbKeysafeEntity keysafeEntity){
        return new AccessData(
                keysafeEntity.getAccessUserName(),
                keysafeEntity.getAccessPassword()
        );
    }

    public static Profile profileBackupFromDbEntity(DbProfileBackupEntity backup){
        return new Profile(
                new ProfileId(
                        backup.getSafeId(),
                        backup.getId()
                ),
                backup.getProfileName(),
                backup.getData(),
                backup.getDataChecksum(),
                backup.getDataDate()
        );
    }

    public static ProfileRevision profileRevisionFromDbEntity(DbProfileRevisionEntity backup){
        return new ProfileRevision(
                new ProfileRevisionId(
                        backup.getSafeId(),
                        backup.getProfileId(),
                        backup.getId()
                ),
                backup.getProfileName(),
                backup.getDataChecksum(),
                backup.getDataDate()
        );
    }

    public static Profile revisionEntryFromDbEntity(DbProfileRevisionEntity backup){
        return new Profile(
                new ProfileId(
                        backup.getSafeId(),
                        backup.getProfileId()
                ),
                backup.getProfileName(),
                backup.getData(),
                backup.getDataChecksum(),
                backup.getDataDate()
        );
    }

    public static List<ProfileRevision> revisionEntriesFromDbEntities(List<DbProfileRevisionEntity> profiles) {
        return profiles.stream().map(ToCoreMapper::profileRevisionFromDbEntity).collect(Collectors.toList());
    }

    public static List<Profile> profileBackupsFromDbEntities(List<DbProfileBackupEntity> profiles) {
        return profiles.stream().map(ToCoreMapper::profileBackupFromDbEntity).collect(Collectors.toList());
    }
}
