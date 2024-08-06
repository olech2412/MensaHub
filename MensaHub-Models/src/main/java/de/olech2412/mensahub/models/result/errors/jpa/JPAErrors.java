package de.olech2412.mensahub.models.result.errors.jpa;

import de.olech2412.mensahub.models.result.errors.ErrorCodes;

public enum JPAErrors implements ErrorCodes {

    UNKNOWN("JPA_400"),
    ERROR_SAVING("JPA_401"),
    ERROR_DELETE("JPA_402"),
    ERROR_UPDATE("JPA_403"),
    ERROR_READ("JPA_404"),
    FOUND_DUPLICATE("JPA_405");

    private final String code;

    JPAErrors(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
