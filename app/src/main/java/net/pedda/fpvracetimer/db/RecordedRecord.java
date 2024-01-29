package net.pedda.fpvracetimer.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recordedrecords")
public class RecordedRecord {
    @PrimaryKey
    public long uid;

    public long droneId;
    public long timestamp;
}

