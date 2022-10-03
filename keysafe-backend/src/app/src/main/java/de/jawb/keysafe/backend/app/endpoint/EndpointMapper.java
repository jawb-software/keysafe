package de.jawb.keysafe.backend.app.endpoint;

import de.jawb.keysafe.backend.api.*;
import de.jawb.keysafe.backend.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EndpointMapper {
    
    private static final PasswordEncoder PW_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public static KeysafeSignup signupToCore(ApiNewKeysafeSignupRequest signup){
        return new KeysafeSignup(
                signup.safeName(),
                signup.accessUserName(),
                PW_ENCODER.encode(signup.accessPassword())
        );
    }

    public static List<ApiProfileInfoResponse> profilesToApi(List<Profile> profiles){
        return profiles.stream().map(p -> new ApiProfileInfoResponse(
                p.id().profileId(),
                p.profileName(),
                p.dataChecksum(),
                p.dataDate()
        )).collect(Collectors.toList());
    }

    public static ApiProfileResponse profileToApi(Profile u){
        return new ApiProfileResponse(u.profileName(), u.data(), u.dataChecksum(), u.dataDate());
    }

    public static NewProfileBackup newProfileToCore(UUID keysafeId, ApiProfileBackupRequest backup) {
        return new NewProfileBackup(keysafeId, backup.profileName(), backup.data(), backup.dataChecksum(), backup.dataDate()) ;
    }

    public static ProfileBackupUpdate profileUpdateToCore(UUID keysafeId, UUID profileId, ApiProfileBackupRequest backup) {
        return new ProfileBackupUpdate(
                new ProfileId(keysafeId, profileId),
                backup.profileName(),
                backup.data(),
                backup.dataChecksum(),
                backup.dataDate()
        );
    }

    public static List<ApiProfileRevisionResponse> revisionsToApi(List<ProfileRevision> history) {
        return history.stream().map(EndpointMapper::revisionToApi).collect(Collectors.toList());
    }

    private static ApiProfileRevisionResponse revisionToApi(ProfileRevision backupInfo){
        return new ApiProfileRevisionResponse(
                backupInfo.id().revisionId(),
                backupInfo.profileName(),
                backupInfo.dataChecksum(),
                backupInfo.dataDate()
        );
    }
}
