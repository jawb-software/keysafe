package de.jawb.keysafe.backend.app.endpoint;

import de.jawb.keysafe.backend.api.KeysafeMonitoringEndpoint;
import de.jawb.keysafe.backend.core.service.KeysafeMonitoringService;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class KeysafeMonitoringEndpointImpl implements KeysafeMonitoringEndpoint {

    @Inject
    private KeysafeMonitoringService monitoringService;

    @Override
    public void ping() {
        monitoringService.checkPersistenceIsReady();
    }
}
