package net.pedda.fpvracetimer.ui.races

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import net.pedda.fpvracetimer.db.DBUtils
import net.pedda.fpvracetimer.db.FPVDb
import net.pedda.fpvracetimer.db.Race
import net.pedda.fpvracetimer.ui.races.RacesRecyclerAdapter.ActiveRaceChangedListener

class RacesViewModel : ViewModel() {
    val races: LiveData<List<Race>>
    private var mAdapter: RacesRecyclerAdapter? = null

    init {
        val fpvDb = FPVDb.getDatabase(null)
        races = fpvDb.raceDao().getAllRacesObservable()
    }

    fun setAdapter(rra: RacesRecyclerAdapter) {
        mAdapter = rra
        rra.attachActiveRaceChangedListener { r: Race?, pos: Int ->
            DBUtils.makeRaceActive(r)
            mAdapter!!.notifyItemChanged(pos)
        }
    }
}