package de.olech2412.mensahub.models.result.errors.api;

import de.olech2412.mensahub.models.result.errors.ErrorCodes;

public enum APIErrors implements ErrorCodes {

    UNKNOWN("API_400"),
    NO_DATA_IN_DATABASE("API_401"),
    INVALID_DATA_IN_DATABASE("API_402");

    private final String code;

    APIErrors(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}