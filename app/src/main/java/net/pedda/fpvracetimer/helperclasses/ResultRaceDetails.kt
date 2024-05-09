package net.pedda.fpvracetimer.helperclasses

import net.pedda.fpvracetimer.db.Drone
import net.pedda.fpvracetimer.db.DroneWithRecords
import net.pedda.fpvracetimer.db.Race
import net.pedda.fpvracetimer.db.RecordedRecord

data class ResultRaceDetails(
    val mRace: Race,
    val mDwR: Map<Drone, List<RecordedRecord>>
)
