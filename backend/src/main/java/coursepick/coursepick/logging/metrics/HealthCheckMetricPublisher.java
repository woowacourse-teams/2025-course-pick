package coursepick.coursepick.logging.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.stereotype.Component;

@Component
public class HealthCheckMetricPublisher {

    private final MeterRegistry meterRegistry;
    private final HealthEndpoint healthEndpoint;

    public HealthCheckMetricPublisher(MeterRegistry meterRegistry, HealthEndpoint healthEndpoint) {
        this.meterRegistry = meterRegistry;
        this.healthEndpoint = healthEndpoint;

        Gauge.builder("health.status",
                () -> healthStatusToNumber(healthEndpoint))
                .description("서버 Health Status: 1=UP, 0=DOWN")
                .register(meterRegistry);
    }

    private static int healthStatusToNumber(HealthEndpoint healthEndpoint) {
        if ("UP".equals(healthEndpoint.health().getStatus().getCode())) {
            return 1;
        }

        return 0;
    }
}
