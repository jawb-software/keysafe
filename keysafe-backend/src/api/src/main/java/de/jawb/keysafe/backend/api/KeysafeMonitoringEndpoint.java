package de.jawb.keysafe.backend.api;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/status")
public interface KeysafeMonitoringEndpoint {

	@Operation(summary = "Backend health state", tags = {"4. Status"})
	@GetMapping
	void ping();

}
