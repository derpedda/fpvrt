package net.pedda.fpvracetimer.db

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Query
import androidx.room.Relation

@Entity(primaryKeys = ["transponderid", "uid"], indices = [Index("transponderid")])
data class RaceDronesCrossRef(
        val uid: Long,
        val transponderid: Long
)


data class RaceWithDrones(
        @Embedded val race: Race,
        @Relation(
                parentColumn = "uid",
                entityColumn = "transponderid",
                associateBy = Junction(RaceDronesCrossRef::class)
        )
        val drones: List<Drone>
)

data class DroneWithRecords(
        @Embedded val drone: Drone,
        @Relation(
                parentColumn = "transponderid",
                entityColumn = "droneId"
        )
        val records: List<RecordedRecordValid>
)
