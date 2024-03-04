package net.pedda.fpvracetimer.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.pedda.fpvracetimer.R
import net.pedda.fpvracetimer.db.DBUtils
import net.pedda.fpvracetimer.db.Drone
import net.pedda.fpvracetimer.db.FPVDb
import net.pedda.fpvracetimer.db.Race
import net.pedda.fpvracetimer.db.helperobjects.DroneDetectedEvent
import net.pedda.fpvracetimer.helperclasses.NoActiveRaceException

class DashboardViewModel() : ViewModel() {
    enum class RaceState {
        PREPARED,
        RUNNING,
        PAUSED,
        STOPPED,
        FINISHED,
        ERROR
    }

    val mRaceLive: LiveData<Race>

    private var mRaceState: RaceState
        get() {
            return mCurrentRace?.raceState ?: RaceState.ERROR
        }
        set(value) {
            mCurrentRace?.raceState = value
            DBUtils.updateRace(mCurrentRace)
        }


    var mRaceChangedListener: CurrentRaceChangedListener? = null

    var mCurrentRace: Race? = null
        set(value) {
            field = value
            mRaceChangedListener?.CurrentRaceChanged(value)
        }

    companion object {
        fun getStateDrawable(state: RaceState): Int {
            when (state) {
                RaceState.PREPARED -> {
                    return R.drawable.baseline_play_arrow_24
                }

                RaceState.RUNNING -> {
                    return R.drawable.ic_outlined_flag_24
                }

                RaceState.STOPPED -> {
                    return R.drawable.baseline_rectangle_24
                }

                RaceState.FINISHED -> {
                    return R.drawable.baseline_check_24
                }

                else -> {
                    return R.drawable.ic_warning
                }
            }
        }
    }
    @Throws(NoActiveRaceException::class)
    fun processRaceControlClick(fab: FloatingActionButton) {
        when (mRaceState) {
            RaceState.PREPARED -> {
                DBUtils.startCurrentRace()
                mRaceState = RaceState.RUNNING
            }
            RaceState.RUNNING -> {
                mRaceState = RaceState.FINISHED
            }

            RaceState.PAUSED -> {}
            RaceState.STOPPED -> {
                mRaceState = RaceState.RUNNING
            }
            RaceState.FINISHED -> {}
            RaceState.ERROR -> {}
        }

        fab.setImageResource(getStateDrawable(mRaceState))
    }

    interface CurrentRaceChangedListener {
        fun CurrentRaceChanged(race: Race?)
    }

    fun droneDetected(timestamp: Long, d: Drone) {
        // calc delta from race start and last detected timestamp
        DBUtils.inputTimingEvent(DroneDetectedEvent(timestamp, d, mCurrentRace))

    }

    init {
        val fpvDb = FPVDb.getDatabase(null)
        mRaceLive = fpvDb.raceDao().currentRaceObservable()
    }
}