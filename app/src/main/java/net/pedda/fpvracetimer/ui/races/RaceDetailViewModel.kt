package net.pedda.fpvracetimer.ui.races

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import net.pedda.fpvracetimer.db.DBUtils
import net.pedda.fpvracetimer.db.Drone
import net.pedda.fpvracetimer.db.DroneWithRecords
import net.pedda.fpvracetimer.db.Race
import net.pedda.fpvracetimer.db.RecordedRecord


class RaceDetailViewModel() : ViewModel() {

    var race: Race = Race()
    var droneswithrecords: Map<Drone,List<RecordedRecord>> = HashMap(0)

    var mDataAvailableListener: RaceDetailsAvailableListener? = null;


    fun initialize(raceid: Long) {

        DBUtils.getRaceAndDronesWithRecords(raceid) {
            race = it.mRace
            droneswithrecords = it.mDwR
            mDataAvailableListener?.raceDetailsAvailable()
        }
    }

    fun interface RaceDetailsAvailableListener {
        fun raceDetailsAvailable()
    }

}
