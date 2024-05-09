package net.pedda.fpvracetimer.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import net.pedda.fpvracetimer.ui.dashboard.DashboardViewModel.RaceState

@Entity(tableName = "races")
data class Race(
        @PrimaryKey(autoGenerate = true)
        val raceid: Long = 0,

        var starttimestamp: Long = 0,

        var racename: String = "",

        var isActive: Boolean = false,

        var raceState: RaceState = RaceState.PREPARED
)