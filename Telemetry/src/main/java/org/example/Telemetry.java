package org.example;


public record Telemetry(
        float distanceMeters,
        float speedKmh,
        float throttle,
        float brake,
        int gear,
        int rpm
) {}