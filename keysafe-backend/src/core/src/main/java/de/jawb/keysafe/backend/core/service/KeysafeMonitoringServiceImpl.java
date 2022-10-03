package de.jawb.keysafe.backend.core.service;

import de.jawb.keysafe.backend.core.service.daos.KeysafeDao;
import de.jawb.keysafe.backend.core.service.exceptions.KeysafeServiceNotReadyException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class KeysafeMonitoringServiceImpl implements KeysafeMonitoringService {

    @Inject
    private KeysafeDao keysafeDao;

    @Override
    public void checkPersistenceIsReady() {
        if(!keysafeDao.checkDb()){
            throw new KeysafeServiceNotReadyException("db");
        }
    }
}
