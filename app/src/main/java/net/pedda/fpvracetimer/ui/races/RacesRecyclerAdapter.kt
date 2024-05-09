package net.pedda.fpvracetimer.ui.races

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.pedda.fpvracetimer.R
import net.pedda.fpvracetimer.databinding.FragmentRacesListBinding
import net.pedda.fpvracetimer.db.Race
import net.pedda.fpvracetimer.ui.dashboard.DashboardViewModel
import java.util.LinkedList

class RacesRecyclerAdapter(
        var detailsRequestedListener: RaceDetailsRequestedListener
) : RecyclerView.Adapter<RacesRecyclerAdapter.ViewHolder>() {

    var dataSet: List<Race> = ArrayList()

    val mListeners: MutableList<ActiveRaceChangedListener> = LinkedList();

    fun attachActiveRaceChangedListener(l: ActiveRaceChangedListener) {
        mListeners.add(l)
    }



    private fun fireActiveRaceChangedListeners(r: Race, pos: Int) {
        for(l: ActiveRaceChangedListener in mListeners) {
            l.onActiveRaceChanged(r, pos);
        }
    }

    class ViewHolder(binding: FragmentRacesListBinding) : RecyclerView.ViewHolder(binding.root) {
        var mIdView: TextView
        var mNameView: TextView
        var mItem: Race? = null
        var mIsActive: ImageView
        var mState: ImageView
        var mDetails: ImageView

        init {
            mIdView = binding.racesTvId
            mNameView = binding.racesTvRacename
            mIsActive = binding.racesIvSelect
            mState = binding.racesIvState
            mDetails = binding.racesIvDetails
        }

        override fun toString(): String {
            return super.toString() + " '" + mNameView.text + "'"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FragmentRacesListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet.get(position)
        holder.mItem = item
        holder.mIdView.text = item.raceid.toString()
        holder.mNameView.text = item.racename
        holder.mIsActive.setImageResource(if (item.isActive) R.drawable.baseline_check_24 else android.R.color.transparent)
        holder.mIsActive.setOnClickListener() {
            fireActiveRaceChangedListeners(item, position)
        }
        holder.mState.setImageResource(DashboardViewModel.getStateDrawable(item.raceState))

        holder.mDetails.setOnClickListener() {
            detailsRequestedListener.onRaceDetailsRequested(item, position)
        }

    }

    override fun getItemCount(): Int {
        return dataSet.size
    }


    companion object {
        const val TAG: String = "RaceRecyclerAdapter"
    }


    fun interface ActiveRaceChangedListener {
        fun onActiveRaceChanged(r: Race, pos: Int)
    }

    fun interface RaceDetailsRequestedListener {
        fun onRaceDetailsRequested(r: Race, pos: Int)
    }
}