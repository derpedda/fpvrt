package net.pedda.fpvracetimer.db

import androidx.annotation.Nullable
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface RecordedRecordDao {

    @Insert
    fun insertRecordedRecord(rr: RecordedRecord)

    @Insert
    fun insertDroneWithRecords(d: Drone, recordList: List<RecordedRecord>)

    @Transaction
    @Query("SELECT * FROM drones WHERE transponderid in (SELECT transponderid FROM recordedrecords WHERE raceId = :raceid)")
    fun getDronesWithRecordsForRace(raceid: Int): List<DroneWithRecords>

    // Retrieve the last row for a race and drone
    @Query("SELECT * FROM recordedrecords WHERE raceId = :raceid AND droneId = :droneid ORDER BY uid DESC LIMIT 1")
    fun getLastRecordForDroneAndRace(raceid: Long, droneid: Long): RecordedRecord?
}
