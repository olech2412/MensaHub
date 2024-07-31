package de.olech2412.mensahub.models.result.errors.parser;

import de.olech2412.mensahub.models.result.errors.Error;
import de.olech2412.mensahub.models.result.errors.ErrorCodes;

/**
 * This class represents an error from the API
 *
 * @since 0.0.1
 */
public record ParserError(String message, ParserErrors parserErrors) implements Error {

    @Override
    public ErrorCodes error() {
        return parserErrors;
    }

    @Override
    public String toString() {
        return "ParserError{" +
                "message='" + message + '\'' +
                ", parserErrors=" + parserErrors.getCode() +
                '}';
    }
}