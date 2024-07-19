package de.olech2412.mensahub.models.result.errors.helper.jobdto;

import de.olech2412.mensahub.models.result.errors.ErrorCodes;

public enum JobDTOErrors implements ErrorCodes {

    UNKNOWN("JOB_C_400"),
    INVALID_CONFIGURATION("JOB_C_401");
    
    private final String code;

    JobDTOErrors(String code){
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}