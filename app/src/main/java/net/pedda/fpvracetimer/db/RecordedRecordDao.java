package net.pedda.fpvracetimer.db;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

@Dao
public interface RecordedRecordDao {
    @Insert
    void insertRecordedRecord(Drone d, RecordedRecord rr);

    @Insert
    void insertDroneWithRecords(Drone d, List<RecordedRecord> recordList);

}
