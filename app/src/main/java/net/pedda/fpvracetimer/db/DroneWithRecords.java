package net.pedda.fpvracetimer.db;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class DroneWithRecords {
    @Embedded public Drone drone;
    @Relation(
            parentColumn = "uid",
            entityColumn = "droneId"
    )
    public List<RecordedRecord> records;


}
