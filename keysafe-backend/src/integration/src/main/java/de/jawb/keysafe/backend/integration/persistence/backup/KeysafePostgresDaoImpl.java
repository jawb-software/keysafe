package de.jawb.keysafe.backend.integration.persistence.backup;

import de.jawb.keysafe.backend.core.*;
import de.jawb.keysafe.backend.core.service.daos.KeysafeDao;
import de.jawb.keysafe.backend.core.service.exceptions.KeysafeNotFoundException;
import de.jawb.keysafe.backend.core.service.exceptions.ProfileNotFoundException;
import de.jawb.keysafe.backend.core.service.exceptions.ProfileRevisionNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@org.springframework.context.annotation.Profile("prod")
@Repository
public class KeysafePostgresDaoImpl implements KeysafeDao {

	private static final Logger logger = LoggerFactory.getLogger(KeysafePostgresDaoImpl.class);

	@PersistenceContext
	private EntityManager em;

	@Transactional
	@Override
	public boolean checkDb() {
		logger.debug("checkDb");
		Query query = em.createNativeQuery("select count(*) from safes");

		try {
			Number result = (Number) query.getSingleResult();
			logger.debug(" -> ok");
			return result != null;
		} catch (Exception e){
			logger.error("Error", e);
		}
		return false;
	}

	@Override
	public AccessData findByAccessUsername(String accessUsername) {
		logger.debug("find keysafe: {}", accessUsername);

		Query query = em.createQuery("select e from DbKeysafeEntity e where e.accessUserName = ?1");
		query.setParameter(1, accessUsername);

		try {

			DbKeysafeEntity e = (DbKeysafeEntity)query.getSingleResult();

			logger.debug(" -> {}", e);

			return ToCoreMapper.accessDataFromDbEntity(e);

		} catch (NoResultException e){
		    logger.error("Error: {}", e.getMessage());
		}

		return null;
	}

	@Transactional
	@Override
	public Profile getProfile(ProfileId backupId) {
		logger.debug("get profile: {}", backupId);

		DbProfileBackupEntity e = getOne(backupId);

		logger.debug(" -> ok");
		return ToCoreMapper.profileBackupFromDbEntity(e);
	}

	@Transactional
	@Override
	public UUID create(KeysafeSignup keysafe) {
		logger.debug("create: {}", keysafe);

		DbKeysafeEntity entity = ToDbEntityMapper.keysafeEntityFromCore(keysafe);

		em.persist(entity);
		em.flush();

		logger.debug(" -> {}", entity.getId());
		return entity.getId();
	}

	@Transactional
	@Override
	public UUID create(NewProfileBackup profile) {
		logger.debug("create: {}", profile);

		if(!keysafeExists(profile.keysafeId())){
			throw new KeysafeNotFoundException(profile.keysafeId());
		}

		DbProfileBackupEntity e = ToDbEntityMapper.profileEntityFromCore(profile);

		em.persist(e);
		em.flush();

		logger.debug(" -> {}", e.getId());
		return e.getId();
	}

	@Transactional
	@Override
	public void update(ProfileBackupUpdate update) {
		logger.debug("update: {}", update);

		DbProfileBackupEntity e = getOne(update.id());

		ToDbEntityMapper.applyChanges(e, update);

		em.persist(e);
		em.flush();

		logger.debug(" -> ok");
	}

	@Transactional
	@Override
	public boolean keysafeExists(UUID keysafeId) {
		logger.debug("keysafe exists: {}", keysafeId);

		Query query = em.createQuery("select count(e) from DbKeysafeEntity e where e.id = ?1");
		query.setParameter(1, keysafeId);

		boolean exists = ((Long)query.getSingleResult() > 0);
		logger.debug(" -> {}", exists);
		return exists;
	}

	@Transactional
	@Override
	public List<ProfileRevision> getRevisions(ProfileId profileId) {
		logger.debug("get history entries: {}", profileId);

		Query query = em.createQuery("select e from DbProfileRevisionEntity e where e.safeId = ?1 AND e.profileId = ?2");
		query.setParameter(1, profileId.keysafeId());
		query.setParameter(2, profileId.profileId());

		List<DbProfileRevisionEntity> resultList = query.getResultList();
		logger.debug(" -> {}", resultList.size());

		return ToCoreMapper.revisionEntriesFromDbEntities(resultList);
	}

	@Transactional
	@Override
	public void createRevision(ProfileId profileIds) {
		logger.debug("history entry will be created via TRIGGER");
	}

	@Override
	public Profile getRevision(ProfileRevisionId id) {
		logger.debug("get revision entry: {}", id);

		Query query = em.createQuery("select e from DbProfileRevisionEntity e where e.safeId = ?1 and e.profileId = ?2 and e.id = ?3");
		query.setParameter(1, id.keysafeId());
		query.setParameter(2, id.profileId());
		query.setParameter(3, id.revisionId());

		try {

			DbProfileRevisionEntity entry = (DbProfileRevisionEntity) query.getSingleResult();
			logger.debug(" -> {}", entry);

			return ToCoreMapper.revisionEntryFromDbEntity(entry);

		} catch (NoResultException e){
			logger.error("Error: {}", e.getMessage());
			throw new ProfileRevisionNotFound(id.revisionId());
		}
	}

	@Transactional
	@Override
	public List<Profile> findAllProfiles(UUID keysafeId) {
		logger.debug("get backup profiles: {}", keysafeId);

		Query query = em.createQuery("select e from DbProfileBackupEntity e where e.safeId = ?1");
		query.setParameter(1, keysafeId);

		List<DbProfileBackupEntity> entries = query.getResultList();

		logger.debug(" -> {}", entries.size());
		return ToCoreMapper.profileBackupsFromDbEntities(entries);
	}

	private DbProfileBackupEntity getOne(ProfileId backupId){
		Query query = em.createQuery("select e from DbProfileBackupEntity e where e.id = ?1 AND e.safeId = ?2");
		query.setParameter(1, backupId.profileId());
		query.setParameter(2, backupId.keysafeId());

		try {

			return (DbProfileBackupEntity) query.getSingleResult();

		} catch (NoResultException e){
			logger.error("Error: {}", e.getMessage());

			if(keysafeExists(backupId.keysafeId())){
				throw new ProfileNotFoundException(backupId);
			} else {
				throw new KeysafeNotFoundException(backupId.keysafeId());
			}
		}
	}
}
