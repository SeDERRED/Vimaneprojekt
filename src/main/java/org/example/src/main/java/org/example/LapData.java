package org.example;

import java.util.List;

public record LapData(
        int lapNumber,
        float lapTime,
        float sector1,
        float sector2,
        float sector3,
        List<Telemetry> telemetry
) {}
