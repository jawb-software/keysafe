package de.jawb.keysafe.backend.api;

public record ApiErrorInfo(int statusCode, String statusMessage, Object reason) {

}
