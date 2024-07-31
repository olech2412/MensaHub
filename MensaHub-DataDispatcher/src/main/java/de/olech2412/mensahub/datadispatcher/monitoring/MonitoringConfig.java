package de.olech2412.mensahub.datadispatcher.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MonitoringConfig {

    private final MeterRegistry meterRegistry;

    public MonitoringConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Counter customCounter(String name, String tagValue, String description) {
        return Counter.builder(name)
                .tag(MonitoringTags.APPLICATION_TAG.getValue(), tagValue)
                .description(description)
                .register(meterRegistry);
    }
}
