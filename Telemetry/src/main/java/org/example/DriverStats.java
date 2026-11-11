package org.example;

import java.util.Locale;

public class DriverStats {
    private int position;
    private int currentLap;
    private int lapsRemaining;
    private float currentTime;
    private float gapAhead;
    private float lastLapTime;

    public DriverStats() {
        this.position = 0;
        this.currentLap = 0;
        this.lapsRemaining = 0;
        this.currentTime = 0.0f;
        this.gapAhead = 0.0f;
        this.lastLapTime = 0.0f;
    }

    public DriverStats(int position, int currentLap, int lapsRemaining, float currentTime, float gapAhead, float lastLapTime) {
        this.position = position;
        this.currentLap = currentLap;
        this.lapsRemaining = lapsRemaining;
        this.currentTime = currentTime;
        this.gapAhead = gapAhead;
        this.lastLapTime = lastLapTime;
    }

    public int getPosition() { return position; }
    public int getCurrentLap() { return currentLap; }
    public int getLapsRemaining() { return lapsRemaining; }
    public float getCurrentTime() { return currentTime; }
    public float getGapAhead() { return gapAhead; }
    public float getLastLapTime() { return lastLapTime; }

    public void update(int position, int currentLap, int lapsRemaining, float currentTime, float gapAhead, float lastLapTime) {
        this.position = position;
        this.currentLap = currentLap;
        this.lapsRemaining = lapsRemaining;
        this.currentTime = currentTime;
        this.gapAhead = gapAhead;
        this.lastLapTime = lastLapTime;
    }

    public static String formatTime(float timeInSeconds) {
        if (timeInSeconds <= 0.0f || Float.isNaN(timeInSeconds) || Float.isInfinite(timeInSeconds)) {
            return "--:--.---";
        }
        int minutes = (int) (timeInSeconds / 60);
        float seconds = timeInSeconds % 60;

        // Передаем Locale.US, чтобы разделителем всегда была точка
        return String.format(Locale.US, "%d:%06.3f", minutes, seconds);
    }
}
