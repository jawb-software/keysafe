package de.jawb.keysafe.backend.api;

import javax.validation.constraints.NotEmpty;

public record ApiNewKeysafeSignupRequest(
        @NotEmpty
        String safeName,
        @NotEmpty
        String accessUserName,
        @NotEmpty
        String accessPassword) {

        @Override
        public String toString() {
                return "ApiNewKeysafeSignup{" +
                        "safeName='" + safeName + '\'' +
                        ", accessUserName='" + accessUserName + '\'' +
                        ", accessPassword='******'" +
                        '}';
        }
}
