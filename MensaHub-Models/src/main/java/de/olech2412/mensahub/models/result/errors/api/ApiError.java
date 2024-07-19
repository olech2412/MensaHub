package de.olech2412.mensahub.models.result.errors.api;

import de.olech2412.mensahub.models.result.errors.Error;
import de.olech2412.mensahub.models.result.errors.ErrorCodes;

/**
 * This class represents an error from the API
 *
 * @since 0.0.1
 */
public record ApiError(String message, APIErrors apiError) implements Error {

    @Override
    public ErrorCodes error() {
        return apiError;
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "message='" + message + '\'' +
                ", jobDTOError=" + apiError.getCode() +
                '}';
    }
}