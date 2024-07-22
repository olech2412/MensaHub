package de.olech2412.mensahub.datadispatcher.monitoring;

import lombok.Getter;

@Getter
public enum MonitoringTags {
    MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG("mensaHub-data-dispatcher"),
    APPLICATION_TAG("application"),
    ;

    private final String value;

    MonitoringTags(String value) {
        this.value = value;
    }
}
