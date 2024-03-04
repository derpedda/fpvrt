package net.pedda.fpvracetimer.helperclasses

import net.pedda.fpvracetimer.db.DroneWithRecords
import net.pedda.fpvracetimer.db.Race

data class ResultRaceDetails(
    val mRace: Race,
    val mDwR: List<DroneWithRecords>
)
