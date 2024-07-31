package de.olech2412.mensahub.models.result.errors.parser;

import de.olech2412.mensahub.models.result.errors.ErrorCodes;

public enum ParserErrors implements ErrorCodes {

    UNKNOWN("PARSER_400");

    private final String code;

    ParserErrors(String code){
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
