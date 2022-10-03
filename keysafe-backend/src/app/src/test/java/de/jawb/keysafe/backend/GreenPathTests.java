package de.jawb.keysafe.backend;

import de.jawb.keysafe.backend.core.*;
import de.jawb.keysafe.backend.core.service.KeysafeService;
import de.jawb.keysafe.backend.core.service.exceptions.BackupUnchangedException;
import de.jawb.tools.security.Hash;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class GreenPathTests {

	@Inject
	private KeysafeService service;

	@Order(1)
	@Test
	public void keysafeCreation() {
		UUID id = service.register(new KeysafeSignup("Test-1", "user-name", "password"));

		Assertions.assertNotNull(id);
	}

	@Order(2)
	@Test
	public void keysafeProfileBackupCreation() {

		UUID keysafeId = service.register(new KeysafeSignup("Test-2", "user-name", "password"));

		Assertions.assertNotNull(keysafeId);

		UUID profileId = service.backupProfile(new NewProfileBackup(
				keysafeId,
				"Test-2-Profile",
				"data",
				Hash.SHA_256("data"),
				LocalDateTime.now()
		));

		Assertions.assertNotNull(profileId);

		Profile backup = service.getById(new ProfileId(keysafeId, profileId));

		Assertions.assertNotNull(backup);
		Assertions.assertEquals(profileId, backup.id().profileId());
	}

	@Order(3)
	@Test
	public void keysafeProfileBackupUpdate() {

		UUID keysafeId = service.register(new KeysafeSignup("Test-3", "user-name", "password"));

		NewProfileBackup firstBackup = new NewProfileBackup(
				keysafeId,
				"Test-3-Profile",
				"data",
				Hash.SHA_256("data"),
				LocalDateTime.now()
		);

		UUID profileId = service.backupProfile(firstBackup);

		Assertions.assertNotNull(profileId);

		final ProfileId profileBackupId = new ProfileId(keysafeId, profileId);

		ProfileBackupUpdate backupUpdate = new ProfileBackupUpdate(
				profileBackupId,
				"Test-3-Profile-Changed",
				"data-changed",
				Hash.SHA_256("data-changed"),
				LocalDateTime.now()
		);

		service.updateProfile(backupUpdate);

		//
		// Erneutes Ändern mit denselben Daten -> Fehler
		//
		{
			Assertions.assertThrowsExactly(BackupUnchangedException.class, () -> {
				service.updateProfile(backupUpdate);
			});
		}

		//
		//
		//
		{
			Profile afterUpdate = service.getById(profileBackupId);

			Assertions.assertEquals(backupUpdate.data(), afterUpdate.data());
			Assertions.assertEquals(backupUpdate.profileName(), afterUpdate.profileName());
			Assertions.assertEquals(backupUpdate.dataChecksum(), afterUpdate.dataChecksum());
		}

		//
		// History
		//
		{
			List<ProfileRevision> history = service.getRevisions(profileBackupId);

			Assertions.assertEquals(1, history.size());

			ProfileRevision historyBackup = history.get(0);

			Assertions.assertEquals(firstBackup.profileName(), historyBackup.profileName());
			Assertions.assertEquals(firstBackup.dataChecksum(), historyBackup.dataChecksum());

		}
	}

}

