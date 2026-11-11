package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TelemetryTest {

    @Test
    void testTelemetryRecordCreation() {
        Telemetry telemetry = new Telemetry(1500.5f, 120.0f, 0.8f, 0.0f, 4, 6500);

        assertEquals(1500.5f, telemetry.distanceMeters(), 0.001f);
        assertEquals(120.0f, telemetry.speedKmh(), 0.001f);
        assertEquals(0.8f, telemetry.throttle(), 0.001f);
        assertEquals(0.0f, telemetry.brake(), 0.001f);
        assertEquals(4, telemetry.gear());
        assertEquals(6500, telemetry.rpm());
    }
}