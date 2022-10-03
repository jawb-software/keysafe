package de.jawb.keysafe.backend.app.endpoint;

import de.jawb.keysafe.backend.api.*;
import de.jawb.keysafe.backend.core.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

class EndpointMapperTest {

    private static final Profile CORE_PROFILE = new Profile(
                                                        new ProfileId(UUID.randomUUID(), UUID.randomUUID()),
                                                        "profileA",
                                                        "data",
                                                        "checksum",
                                                        LocalDateTime.now()
                                                    );

    @Test
    void testSignupToDomain() {

        ApiNewKeysafeSignupRequest signup = new ApiNewKeysafeSignupRequest("safeName", "keysafe", "keysafe");
        KeysafeSignup domainSignup = EndpointMapper.signupToCore(signup);

        Assertions.assertEquals(signup.safeName(), domainSignup.name());
        Assertions.assertEquals(signup.accessUserName(), domainSignup.accessUserName());
        Assertions.assertNotEquals(signup.accessPassword(), domainSignup.accessPassword());
        Assertions.assertTrue(domainSignup.accessPassword().startsWith("{bcrypt}"));

    }

    @Test
    void testProfilesToApi() {

        List<ApiProfileInfoResponse> apiProfileInfos = EndpointMapper.profilesToApi(List.of(CORE_PROFILE));

        Assertions.assertEquals(1, apiProfileInfos.size());

        ApiProfileInfoResponse apiProfileInfo = apiProfileInfos.get(0);

        Assertions.assertEquals(CORE_PROFILE.profileName(), apiProfileInfo.profileName());
        Assertions.assertEquals(CORE_PROFILE.dataChecksum(), apiProfileInfo.dataChecksum());
        Assertions.assertEquals(CORE_PROFILE.dataDate(), apiProfileInfo.dataDate());
    }

    @Test
    void testProfileToApi() {

        ApiProfileResponse apiProfileResponse = EndpointMapper.profileToApi(CORE_PROFILE);

        Assertions.assertEquals(CORE_PROFILE.profileName(), apiProfileResponse.profileName());
        Assertions.assertEquals(CORE_PROFILE.data(), apiProfileResponse.data());
        Assertions.assertEquals(CORE_PROFILE.dataChecksum(), apiProfileResponse.dataChecksum());
        Assertions.assertEquals(CORE_PROFILE.dataDate(), apiProfileResponse.dataDate());

    }

    @Test
    void testNewProfileToDomain() {

        ApiProfileBackupRequest apiBackup = new ApiProfileBackupRequest(
                "profile",
                "data",
                "checksum",
                LocalDateTime.now()
        );
        NewProfileBackup coreBackup = EndpointMapper.newProfileToCore(UUID.randomUUID(), apiBackup);

        Assertions.assertEquals(apiBackup.profileName(), coreBackup.profileName());
        Assertions.assertEquals(apiBackup.data(), coreBackup.data());
        Assertions.assertEquals(apiBackup.dataChecksum(), coreBackup.dataChecksum());
        Assertions.assertEquals(apiBackup.dataDate(), coreBackup.dataDate());
    }

    @Test
    void testProfileUpdateToDomain() {

        ApiProfileBackupRequest apiBackup = new ApiProfileBackupRequest(
                "profile",
                "data",
                "checksum",
                LocalDateTime.now()
        );

        ProfileBackupUpdate coreProfileUpdate = EndpointMapper.profileUpdateToCore(UUID.randomUUID(), UUID.randomUUID(), apiBackup);

        Assertions.assertEquals(apiBackup.profileName(), coreProfileUpdate.profileName());
        Assertions.assertEquals(apiBackup.data(), coreProfileUpdate.data());
        Assertions.assertEquals(apiBackup.dataChecksum(), coreProfileUpdate.dataChecksum());
        Assertions.assertEquals(apiBackup.dataDate(), coreProfileUpdate.dataDate());
    }

    @Test
    void testRevisionsToApi() {

        ProfileRevisionId id = new ProfileRevisionId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        ProfileRevision revision = new ProfileRevision(
                id,
                "profileName",
                "checksum",
                LocalDateTime.now()
        );

        List<ApiProfileRevisionResponse> apiProfileRevisions = EndpointMapper.revisionsToApi(List.of(revision));

        Assertions.assertEquals(1, apiProfileRevisions.size());

        ApiProfileRevisionResponse apiRevision = apiProfileRevisions.get(0);

        Assertions.assertEquals(revision.profileName(), apiRevision.profileName());
        Assertions.assertEquals(revision.dataChecksum(), apiRevision.dataChecksum());
        Assertions.assertEquals(revision.dataDate(), apiRevision.dataDate());
        Assertions.assertEquals(id.revisionId(), apiRevision.revisionId());

    }
}