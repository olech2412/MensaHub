package de.olech2412.mensahub.models.result.errors.mail;

import de.olech2412.mensahub.models.result.errors.Error;
import de.olech2412.mensahub.models.result.errors.ErrorCodes;

/**
 * This class represents an error from the API
 *
 * @since 0.0.1
 */
public record MailError(String message, MailErrors jpaError) implements Error {

    @Override
    public ErrorCodes error() {
        return jpaError;
    }

    @Override
    public String toString() {
        return "MailError{" +
                "message='" + message + '\'' +
                ", parserErrors=" + jpaError.getCode() +
                '}';
    }
}