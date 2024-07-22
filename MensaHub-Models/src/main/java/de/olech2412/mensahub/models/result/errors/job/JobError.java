package de.olech2412.mensahub.models.result.errors.job;

import de.olech2412.mensahub.models.result.errors.Error;
import de.olech2412.mensahub.models.result.errors.ErrorCodes;

public record JobError(String message, JobErrors jobDTOError) implements Error {

    @Override
    public ErrorCodes error() {
        return jobDTOError;
    }

    @Override
    public String toString() {
        return "JobDTOError{" +
                "message='" + message + '\'' +
                ", jobError=" + jobDTOError.getCode() +
                '}';
    }
}