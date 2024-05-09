package net.pedda.fpvracetimer.ui.races.raceview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.pedda.fpvracetimer.databinding.RacesDroneDetailBinding
import net.pedda.fpvracetimer.db.Drone
import net.pedda.fpvracetimer.db.RecordedRecord

class DroneWithRecordsAdapter(
        val records: List<RecordedRecord>?,
        val drone: Drone
) : RecyclerView.Adapter<DroneWithRecordsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RacesDroneDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return records!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records!!.get(position)

        holder.lapId.text = record.uid.toString()
        // calc the lap
        val ms = record.duration
        val seconds = ms / 1000F
        val totalminutes = (seconds / 60).toLong()
        val remainingSeconds = (((ms/1000) % 1000L) % 60).toLong()
        val remainingMilliseconds = (ms % 1000L).toLong()

        holder.lapTime.text = String.format("%02d:%02d.%03d", totalminutes, remainingSeconds, remainingMilliseconds)

    }

    class ViewHolder(binding: RacesDroneDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        var lapId: TextView
        var lapTime: TextView

        init {
            lapId = binding.racedetailLapId
            lapTime = binding.racedetailLapTime
        }

    }


}