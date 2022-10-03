package de.jawb.keysafe.backend.integration.persistence.backup;

import de.jawb.keysafe.backend.core.*;
import de.jawb.keysafe.backend.core.service.daos.KeysafeDao;
import de.jawb.keysafe.backend.core.service.exceptions.KeysafeNotFoundException;
import de.jawb.keysafe.backend.core.service.exceptions.ProfileNotFoundException;
import de.jawb.keysafe.backend.core.service.exceptions.ProfileRevisionNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@org.springframework.context.annotation.Profile("in-memory-db")
@Repository
public class KeysafeInMemDaoImpl implements KeysafeDao {

	private static final Logger logger = LoggerFactory.getLogger(KeysafeInMemDaoImpl.class);

    private final Map<ProfileId, Profile> 		 profileBackups = new HashMap<>();
    private final Map<UUID, KeysafeSignup>       keysafes       = new HashMap<>();
	private final Map<ProfileId, List<Revision>> history  		= new HashMap<>();

	private static record Revision(UUID id, Profile backup){}

	@Inject
	private PasswordEncoder encoder;

	@PostConstruct
	private void init(){
		UUID keysafeId = create(new KeysafeSignup("Test", "keysafe", encoder.encode("keysafe")));
		UUID profileId = create(new NewProfileBackup(
				keysafeId,
				"Profil-name",
				"data",
				"checksum",
				LocalDateTime.now()
		));

		ProfileId id = new ProfileId(keysafeId, profileId);
		update(new ProfileBackupUpdate(
				id,
				"profile-name-2",
				"data-2",
				"checksum-2",
				LocalDateTime.now()
		));
		createRevision(id);
	}

	@Override
	public boolean checkDb() {
		logger.debug("checkDb: true");
		return true;
	}

	@Override
	public AccessData findByAccessUsername(String accessUsername) {
		logger.debug("findByAccessUsername: {}", accessUsername);

		for(Entry<UUID, KeysafeSignup> e : keysafes.entrySet()){
			if(e.getValue().accessUserName().equals(accessUsername)){

				logger.debug(" -> found {}", e.getValue());
				return new AccessData(
						e.getValue().accessUserName(),
						e.getValue().accessPassword()
				);
			}
		}

		logger.warn(" -> nothing found for {}", accessUsername);
		return null;
	}

	@Override
	public List<Profile> findAllProfiles(UUID keysafeId) {
		logger.debug("find all profiles: {}", keysafeId);

		Collection<Profile> backups = profileBackups.values();

		logger.debug(" -> {}", backups);
		return backups.stream()
				.filter(p -> p.id().keysafeId().equals(keysafeId))
				.collect(Collectors.toList());
	}

	@Override
	public Profile getProfile(ProfileId id) {
		logger.debug("get profile: {}", id);

		if(!keysafes.containsKey(id.keysafeId())){
			logger.error("keysafe {} not found", id.keysafeId());
			throw new KeysafeNotFoundException(id.keysafeId());
		}

		Profile backup = profileBackups.get(id);

		if(backup == null){
			logger.error("profile {} not found in keysafe {}", id.profileId(), id.keysafeId());
			throw new ProfileNotFoundException(id);
		}

		logger.debug(" -> {}", backup);
		return backup;
	}

	@Override
	public UUID create(KeysafeSignup keysafe) {
		logger.debug("create: {}", keysafe);

		UUID id = keysafes.isEmpty() ? UUID.fromString("4e6d3310-111e-402a-89d0-31aa4cb3ea24") : UUID.randomUUID();

		keysafes.put(id, keysafe);

		logger.debug(" -> {}", id);

		return id;
	}

	@Override
	public UUID create(NewProfileBackup backup) {
		logger.debug("create: {}", backup);

		if(!keysafes.containsKey(backup.keysafeId())){
			throw new KeysafeNotFoundException(backup.keysafeId());
		}

		UUID profileId = profileBackups.isEmpty() ? UUID.fromString("e7ae3e80-4c4d-458e-b6bb-7105bfdfbd8d") : UUID.randomUUID();

		profileBackups.put(new ProfileId(backup.keysafeId(), profileId), new Profile(
				new ProfileId(
						backup.keysafeId(),
						profileId
				),
				backup.profileName(),
				backup.data(),
				backup.dataChecksum(),
				backup.dataDate()
		));
		logger.debug(" -> {}", profileId);
		return profileId;
	}

	@Override
	public void update(ProfileBackupUpdate update) {
		logger.debug("update: {}", update);

		UUID keysafeId = update.id().keysafeId();

		if(!keysafeExists(keysafeId)){
			throw new KeysafeNotFoundException(keysafeId);
		}

		Profile oldState = profileBackups.get(update.id());
		logger.debug("old state: {}", oldState);

		if(oldState == null){
			throw new ProfileNotFoundException(update.id());
		}

		profileBackups.put(update.id(), new Profile(
				update.id(),
				update.profileName(),
				update.data(),
				update.dataChecksum(),
				update.dataDate()
		));
		logger.debug(" -> OK");
	}

	@Override
	public boolean keysafeExists(UUID keysafeId) {
		logger.debug("keysafe exists: {}", keysafeId);
		boolean b = keysafes.containsKey(keysafeId);

		logger.debug(" -> {}", b);
		return b;
	}

	@Override
	public List<ProfileRevision> getRevisions(ProfileId profileId) {
		logger.debug("get history entries: {}", profileId);
		return history.getOrDefault(profileId, new ArrayList<>()).stream().map(
				e -> new ProfileRevision(
						new ProfileRevisionId(
								e.backup().id().keysafeId(),
								e.backup().id().profileId(),
								e.id
						),
						e.backup().profileName(),
						e.backup().dataChecksum(),
						e.backup().dataDate()
				)
		).collect(Collectors.toList());
	}

	@Override
	public void createRevision(ProfileId profileId) {
		logger.debug("create revision entry: {}", profileId);

		if(!keysafeExists(profileId.keysafeId())){
			throw new KeysafeNotFoundException(profileId.keysafeId());
		}

		Profile oldState = getProfile(profileId);

		if(oldState == null){
			throw new ProfileNotFoundException(profileId);
		}

		List<Revision> list = history.computeIfAbsent(profileId, k -> new ArrayList<>());

		logger.debug("created new Revision entry for: {}", profileId);

		UUID id = list.isEmpty() ? UUID.fromString("7175d663-0c4a-4e16-bed0-2cdfd4de0681") : UUID.randomUUID();

		list.add(new Revision(id, oldState));

		logger.debug(" -> {}", id);

	}

	@Override
	public Profile getRevision(ProfileRevisionId id) {
		logger.debug("get revision: {}", id);

		if(!keysafeExists(id.keysafeId())){
			throw new KeysafeNotFoundException(id.keysafeId());
		}

		ProfileId profileId = new ProfileId(id.keysafeId(), id.profileId());
		Profile oldState 	= getProfile(profileId);

		if(oldState == null){
			throw new ProfileNotFoundException(profileId);
		}

		Collection<List<Revision>> values = history.values();
		for(List<Revision> revisions : values){
			for(Revision r : revisions){
				if(id.revisionId().equals(r.id)){
					logger.debug(" -> {}", r.backup);
					return r.backup;
				}
			}
		}

		logger.error("nothing found for {}", id);
		throw new ProfileRevisionNotFound(id.revisionId());
	}

}
