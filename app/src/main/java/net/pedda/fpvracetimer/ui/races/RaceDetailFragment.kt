package net.pedda.fpvracetimer.ui.races

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import net.pedda.fpvracetimer.R
import net.pedda.fpvracetimer.ui.races.raceview.RaceDetailAdapter

class RaceDetailFragment : Fragment() {

    var mRecycler: RecyclerView? = null
    var mRaceNameTv: TextView? = null

    var raceid: Long = -1;

    var mAdapter: RaceDetailAdapter = RaceDetailAdapter()

    private lateinit var viewModel: RaceDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_race_detail, container, false)

        mRecycler = root.findViewById(R.id.racedetail_dronerecycler)
        mRecycler?.adapter = mAdapter

        mRaceNameTv = root.findViewById(R.id.racedetail_racename)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if(menuItem.itemId == android.R.id.home) {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)

        return root
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        raceid = arguments?.getLong("raceid")!!

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