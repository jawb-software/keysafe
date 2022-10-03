package de.jawb.keysafe.backend.api;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ApiProfileBackupRequest(
		@NotEmpty String profileName,
		@NotEmpty String data,
		@NotEmpty String dataChecksum,

		@Schema(format = "dd.MM.yyyy, HH:mm:ss")
		@NotNull
		LocalDateTime dataDate) {
}
