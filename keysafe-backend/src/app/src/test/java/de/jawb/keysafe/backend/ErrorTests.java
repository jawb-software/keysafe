package de.jawb.keysafe.backend;

import de.jawb.keysafe.backend.core.*;
import de.jawb.keysafe.backend.core.service.KeysafeService;
import de.jawb.keysafe.backend.core.service.exceptions.KeysafeNotFoundException;
import de.jawb.keysafe.backend.core.service.exceptions.ProfileNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ErrorTests {

	@Inject
	private KeysafeService service;

	@Test
	public void keysafeIdUnknown() {
		Assertions.assertThrowsExactly(KeysafeNotFoundException.class, () -> {
			Profile backup = service.getById(new ProfileId(UUID.randomUUID(), UUID.randomUUID()));
		});

		Assertions.assertThrowsExactly(KeysafeNotFoundException.class, () -> {
			UUID id = service.backupProfile(new NewProfileBackup(
					UUID.randomUUID(),
					"Test",
					"data",
					"dataChecksum",
					LocalDateTime.now()
			));
		});
	}

	@Test
	public void profileIdUnknown() {
		UUID keysafeId = service.register(new KeysafeSignup("test", "user-name", "password"));

		Assertions.assertThrowsExactly(ProfileNotFoundException.class, () -> {
			Profile backup = service.getById(new ProfileId(keysafeId, UUID.randomUUID()));
		});

		Assertions.assertThrowsExactly(ProfileNotFoundException.class, () -> {
			service.updateProfile(new ProfileBackupUpdate(
					new ProfileId(
							keysafeId,
							UUID.randomUUID()
					),
					"",
					"",
					"",
					LocalDateTime.now())
			);
		});
	}

}

