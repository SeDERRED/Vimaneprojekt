package org.example;

public class Participant {
    private int position;
    private float currentTime;
    private float lastLapTime;

    public Participant( int position, float currentTime, float lastLapTime) {
        this.position = position;
        this.currentTime = currentTime;
        this.lastLapTime = lastLapTime;
    }

    public int getPosition() { return position; }
    public float getCurrentTime() { return currentTime; }
    public float getLastLapTime() { return lastLapTime; }

    public void update(int position, float currentTime, float lastLapTime) {
        this.position = position;
        this.currentTime = currentTime;
        this.lastLapTime = lastLapTime;
    }
}