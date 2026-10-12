package org.example;

public class Participant {
    private final String name;
    private int position;
    private float gapToAhead;
    private float lastLapTime;
    private boolean inPits;

    public Participant(String name, int position, float gapToAhead, float lastLapTime, boolean inPits) {
        this.name = name;
        this.position = position;
        this.gapToAhead = gapToAhead;
        this.lastLapTime = lastLapTime;
        this.inPits = inPits;
    }

    public String getName() { return name; }
    public int getPosition() { return position; }
    public float getGapToAhead() { return gapToAhead; }
    public float getLastLapTime() { return lastLapTime; }
    public boolean isInPits() { return inPits; }

    public void update(int position, float gapToAhead, float lastLapTime, boolean inPits) {
        this.position = position;
        this.gapToAhead = gapToAhead;
        this.lastLapTime = lastLapTime;
        this.inPits = inPits;
    }
}

