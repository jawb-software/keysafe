package de.jawb.keysafe.backend.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@OpenAPIDefinition( info = @Info(title = "keysafe backend app - Open Api"))
@RestController
@RequestMapping(value = "/keysafes", consumes = "application/json", produces = "application/json")
public interface KeysafeEndpoint {

	@Operation(summary = "Register device and create new keysafe", tags = {"1. Signup"})
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	ApiEntityInfo register(@RequestBody @Valid ApiNewKeysafeSignupRequest signup);

	@Operation(summary = "Get profile backup", tags = {"2. Backup"})
	@GetMapping(path = "/{keysafeId}/profiles/{profileId}")
	ApiProfileResponse getProfile(@PathVariable UUID keysafeId, @PathVariable UUID profileId);

	@Operation(summary = "Get all profiles in this keysafe", tags = {"2. Backup"})
	@GetMapping(path = "/{keysafeId}/profiles")
	List<ApiProfileInfoResponse> getProfiles(@PathVariable UUID keysafeId);

	@Operation(summary = "Save new profile backup", tags = {"2. Backup"})
	@PostMapping(path = "/{keysafeId}/profiles")
	@ResponseStatus(HttpStatus.CREATED)
	ApiEntityInfo addProfileBackup(@PathVariable UUID keysafeId, @RequestBody @Valid ApiProfileBackupRequest profileBackup);

	@Operation(summary = "Update profile backup", tags = {"2. Backup"})
	@PutMapping(path = "/{keysafeId}/profiles/{profileId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void updateProfileBackup(@PathVariable UUID keysafeId, @PathVariable UUID profileId, @RequestBody @Valid ApiProfileBackupRequest profileBackup);

	@Operation(summary = "Get profile revision", tags = {"3. Revisions"})
	@GetMapping(path = "/{keysafeId}/profiles/{profileId}/revisions/{revisionId}")
	ApiProfileResponse getProfileRevision(@PathVariable UUID keysafeId, @PathVariable UUID profileId, @PathVariable UUID revisionId);

	@Operation(summary = "Get all profile revisions", tags = {"3. Revisions"})
	@GetMapping(path = "/{keysafeId}/profiles/{profileId}/revisions")
	List<ApiProfileRevisionResponse> getProfileRevisions(@PathVariable UUID keysafeId, @PathVariable UUID profileId);
}
