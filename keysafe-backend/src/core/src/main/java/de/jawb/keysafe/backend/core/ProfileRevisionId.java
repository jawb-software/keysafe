package de.jawb.keysafe.backend.core;

import java.util.UUID;

public record ProfileRevisionId(UUID keysafeId, UUID profileId, UUID revisionId){}
