package net.pedda.fpvracetimer.db

import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordedrecords")
data class RecordedRecord(
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0,
    var droneId: Long = 0,
    var duration: Long = 0,
    var timestamp: Long = 0,
    var isFirst: Boolean = false,
    var raceId: Long = 0
)

@DatabaseView(" SELECT * FROM recordedrecords WHERE isFirst == 0;")
data class RecordedRecordValid(
        @PrimaryKey(autoGenerate = true)
        var uid: Long = 0,
        var droneId: Long = 0,
        var duration: Long = 0,
        var timestamp: Long = 0,
        var isFirst: Boolean = false,
        var raceId: Long = 0
)