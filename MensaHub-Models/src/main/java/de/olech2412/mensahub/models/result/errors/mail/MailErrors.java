package de.olech2412.mensahub.models.result.errors.mail;

import de.olech2412.mensahub.models.result.errors.ErrorCodes;

public enum MailErrors implements ErrorCodes {

    UNKNOWN("MAIL_400");

    private final String code;

    MailErrors(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
