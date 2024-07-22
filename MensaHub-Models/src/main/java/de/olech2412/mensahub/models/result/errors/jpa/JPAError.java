package de.olech2412.mensahub.models.result.errors.jpa;

import de.olech2412.mensahub.models.result.errors.Error;
import de.olech2412.mensahub.models.result.errors.ErrorCodes;

/**
 * This class represents an error from the API
 *
 * @since 0.0.1
 */
public record JPAError(String message, JPAErrors jpaError) implements Error {

    @Override
    public ErrorCodes error() {
        return jpaError;
    }

    @Override
    public String toString() {
        return "JPAError{" +
                "message='" + message + '\'' +
                ", jobDTOError=" + jpaError.getCode() +
                '}';
    }
}