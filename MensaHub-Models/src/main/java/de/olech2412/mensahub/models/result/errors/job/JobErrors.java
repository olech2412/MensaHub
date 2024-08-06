package de.olech2412.mensahub.models.result.errors.job;

import de.olech2412.mensahub.models.result.errors.ErrorCodes;

public enum JobErrors implements ErrorCodes {

    UNKNOWN("JOB_C_400"),
    INVALID_CONFIGURATION("JOB_C_401");

    private final String code;

    JobErrors(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}