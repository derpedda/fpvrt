package net.pedda.fpvracetimer.ui.races

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import net.pedda.fpvracetimer.R
import net.pedda.fpvracetimer.ui.races.raceview.RaceDetailAdapter

class RaceDetailFragment : Fragment() {

    var mRecycler: RecyclerView? = null
    var mRaceNameTv: TextView? = null

    var raceid = -1;

    var mAdapter: RaceDetailAdapter = RaceDetailAdapter()

//    companion object {
//        fun newInstance() = RaceDetailFragment()
//    }

    private lateinit var viewModel: RaceDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_race_detail, container, false)

        mRecycler = root.findViewById(R.id.racedetail_dronerecycler)
        mRecycler?.adapter = mAdapter

        mRaceNameTv = root.findViewById(R.id.racedetail_racename)

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        raceid = arguments?.getInt("raceid")!!

        viewModel = ViewModelProvider(this)[RaceDetailViewModel::class.java]

        viewModel.mDataAvailableListener = RaceDetailViewModel.RaceDetailsAvailableListener() {
            requireActivity().runOnUiThread() {
                mAdapter.drones = viewModel.droneswithrecords
                mRaceNameTv?.text = viewModel.race.racename
            }
        }
        viewModel.initialize(raceid)



    }





}