package de.olech2412.mensahub.request;

import lombok.Getter;

public enum RequestPath {

    PREDICT("/predict");

    @Getter
    private final String path;

    RequestPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }
}