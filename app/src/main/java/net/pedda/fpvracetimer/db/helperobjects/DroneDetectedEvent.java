package net.pedda.fpvracetimer.db.helperobjects;

import net.pedda.fpvracetimer.db.Drone;
import net.pedda.fpvracetimer.db.Race;

public class DroneDetectedEvent {

    private long timestamp;
    private Drone detectedDrone;

    private Race race;


    public DroneDetectedEvent(long timestamp, Drone detectedDrone, Race currentRace) {
        this.timestamp = timestamp;
        this.detectedDrone = detectedDrone;
        this.race = currentRace;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Drone getDetectedDrone() {
        return detectedDrone;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public void setDetectedDrone(Drone detectedDrone) {
        this.detectedDrone = detectedDrone;
    }
}