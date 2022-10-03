package de.jawb.keysafe.backend.api;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
public record ApiCallerInfo(String requestId, String userUniqueId, String ip) {

    @Override
    public String toString() {
        return requestId + "/" + userUniqueId + "/" + ip;
    }

}
