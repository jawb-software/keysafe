package de.jawb.keysafe.backend.app.endpoint;

import de.jawb.keysafe.backend.api.*;
import de.jawb.keysafe.backend.core.Profile;
import de.jawb.keysafe.backend.core.ProfileId;
import de.jawb.keysafe.backend.core.ProfileRevision;
import de.jawb.keysafe.backend.core.ProfileRevisionId;
import de.jawb.keysafe.backend.core.service.KeysafeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@RestController
public class KeysafeEndpointImpl implements KeysafeEndpoint {

	private static final Logger logger = LoggerFactory.getLogger(KeysafeEndpointImpl.class);

	@Inject
	private KeysafeService keysafeService;

	@Override
	public ApiEntityInfo register(ApiNewKeysafeSignupRequest signup) {
		logger.info("calling register: {}", signup);
		UUID keysafeId = keysafeService.register(EndpointMapper.signupToCore(signup));

		return new ApiEntityInfo(keysafeId);
	}

	@Override
	public ApiProfileResponse getProfile(UUID keysafeId, UUID profileId) {
		logger.info("calling get profile: {} in {}", profileId, keysafeId);

		Profile backup = keysafeService.getById(new ProfileId(keysafeId, profileId));

		return EndpointMapper.profileToApi(backup);
	}

	@Override
	public List<ApiProfileInfoResponse> getProfiles(UUID keysafeId) {
		logger.info("calling get all profiles for keysafe: {}", keysafeId);

		List<Profile> profiles = keysafeService.getProfiles(keysafeId);

		return EndpointMapper.profilesToApi(profiles);
	}

	@Override
	public ApiEntityInfo addProfileBackup(UUID keysafeId, ApiProfileBackupRequest profileBackup) {
		logger.info("calling add profile backup: {} - {}", keysafeId, profileBackup);

		UUID profileId = keysafeService.backupProfile(EndpointMapper.newProfileToCore(keysafeId, profileBackup));

		return new ApiEntityInfo(profileId);
	}

	@Override
	public void updateProfileBackup(UUID keysafeId, UUID profileId, ApiProfileBackupRequest backup) {
		logger.info("calling update profile backup: {}/{} - {}", keysafeId, profileId, backup);

		keysafeService.updateProfile(EndpointMapper.profileUpdateToCore(keysafeId, profileId, backup));
	}

	@Override
	public ApiProfileResponse getProfileRevision(UUID keysafeId, UUID profileId, UUID revisionId) {
		logger.info("calling get profile revision: {}, {}, {}", keysafeId, profileId, revisionId);

		Profile backup = keysafeService.getProfileRevision(new ProfileRevisionId(keysafeId, profileId, revisionId));

		return EndpointMapper.profileToApi(backup);
	}

	@Override
	public List<ApiProfileRevisionResponse> getProfileRevisions(UUID keysafeId, UUID profileId) {
		logger.info("calling get profile revisions: {}, {}", keysafeId, profileId);

		List<ProfileRevision> revisions = keysafeService.getRevisions(new ProfileId(keysafeId, profileId));

		return EndpointMapper.revisionsToApi(revisions);
	}

}
