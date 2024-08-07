package de.olech2412.mensahub.models.result.errors.api;

import de.olech2412.mensahub.models.result.errors.Error;
import de.olech2412.mensahub.models.result.errors.ErrorCodes;
import de.olech2412.mensahub.models.result.errors.job.JobErrors;

public record APIError(String message, APIErrors apiErrors) implements Error {

    @Override
    public ErrorCodes error() {
        return apiErrors;
    }

    @Override
    public String toString() {
        return "APIError{" +
                "message='" + message + '\'' +
                ", apiError=" + apiErrors.getCode() +
                '}';
    }
}