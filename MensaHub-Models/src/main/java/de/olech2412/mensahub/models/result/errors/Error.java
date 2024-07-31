package de.olech2412.mensahub.models.result.errors;

/**
 * This class represents an general error and can be implemented by other classes
 *
 * @since 0.0.1
 */
public interface Error {

    String message();

    ErrorCodes error();

}