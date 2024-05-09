package net.pedda.fpvracetimer.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "recordedrecords")
data class RecordedRecord(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    var droneId: Long = 0,
    var duration: Long = 0,
    var timestamp: Long = 0,
    var isFirst: Boolean = false,

    var raceId: Long = 0
)

//@DatabaseView(" SELECT * FROM recordedrecords WHERE isFirst == 0;")
//data class RecordedRecordValid(
//        @PrimaryKey(autoGenerate = true)
//        val uid: Long = 0,
//        var droneId: Long = 0,
//        var duration: Long = 0,
//        var timestamp: Long = 0,
//        var isFirst: Boolean = false,
//        var raceId: Long = 0
//)