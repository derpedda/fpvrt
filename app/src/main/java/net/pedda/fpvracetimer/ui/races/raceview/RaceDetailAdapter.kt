package net.pedda.fpvracetimer.ui.races.raceview

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.pedda.fpvracetimer.databinding.FragmentRaceDetailBinding
import net.pedda.fpvracetimer.databinding.FragmentRaceDetaildronesBinding
import net.pedda.fpvracetimer.databinding.RacesDroneDetailBinding
import net.pedda.fpvracetimer.db.DroneWithRecords


class RaceDetailAdapter() : RecyclerView.Adapter<RaceDetailAdapter.ViewHolder>() {


    var drones: List<DroneWithRecords> = ArrayList(0)
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FragmentRaceDetaildronesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return drones.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dwr = drones.get(position)

        holder.droneName.text = dwr.drone.dronename
        holder.lapRecycler.adapter = DroneWithRecordsAdapter(drone = dwr.drone, records = dwr.records)

    }

    class ViewHolder(binding: FragmentRaceDetaildronesBinding) : RecyclerView.ViewHolder(binding.root) {
        var droneName: TextView
        var lapRecycler: RecyclerView

        var targetAreaStart: Int = 0
        var targetAreaEnd: Int = 0

        init {
            droneName = binding.racedetailDronename
            lapRecycler = binding.racedetailLaprecycler

            lapRecycler.addOnItemTouchListener(
                    object : RecyclerView.OnItemTouchListener {
                        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                            calculateTouchArea(lapRecycler)

                            val isTouchOnTargetArea =
                                    e.rawY.toInt() in targetAreaStart..targetAreaEnd

                            if (isTouchOnTargetArea)
                                binding.racedetailLaprecycler
                                        .parent
                                        .requestDisallowInterceptTouchEvent(true)
                            else
                                binding.racedetailDronename
                                        .parent
                                        .requestDisallowInterceptTouchEvent(false)

                            return false
                        }

                        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                            //ignore
                        }

                        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                            //ignore
                        }
                    },
            )
        }


        private fun calculateTouchArea(rv: RecyclerView) {

            val array = IntArray(2)
            rv.getLocationOnScreen(array)
            val y = array[1]
            val height = rv.measuredHeight
            targetAreaStart = y + height / 2
            targetAreaEnd = targetAreaStart + height / 2

        }

    }


}