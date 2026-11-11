package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DriverStatsTest {

    @Test
    void testDriverStatsGettersAndConstructor() {
        DriverStats stats = new DriverStats(1, 3, 5, 85.5f, 2.1f, 92.0f);

        assertEquals(1, stats.getPosition());
        assertEquals(3, stats.getCurrentLap());
        assertEquals(5, stats.getLapsRemaining());
        assertEquals(85.5f, stats.getCurrentTime(), 0.001f);
        assertEquals(2.1f, stats.getGapAhead(), 0.001f);
        assertEquals(92.0f, stats.getLastLapTime(), 0.001f);
    }

    @Test
    void testFormatTimeValid() {
        // 83.456 секунд -> 1 минута 23.456 секунды
        String formatted = DriverStats.formatTime(83.456f);
        assertEquals("1:23.456", formatted);
    }

    @Test
    void testFormatTimeInvalid() {
        assertEquals("--:--.---", DriverStats.formatTime(0.0f));
        assertEquals("--:--.---", DriverStats.formatTime(-5.0f));
        assertEquals("--:--.---", DriverStats.formatTime(Float.NaN));
    }
}