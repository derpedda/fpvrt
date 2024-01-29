package net.pedda.fpvracetimer.ui.droneconfig;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.pedda.fpvracetimer.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A fragment representing a list of Items.
 */
public class DroneFragment extends Fragment {

    // TODO: Customize parameters
    private int mColumnCount = 1;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";


    private BeaconManager beaconManager;

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DroneFragment newInstance(int columnCount) {
        DroneFragment fragment = new DroneFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DroneFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drone_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();

            MyDroneRecyclerViewAdapter adapter = new MyDroneRecyclerViewAdapter(DroneItemContent.ITEMS);

            beaconManager = BeaconManager.getInstanceForApplication(this.requireContext());
            beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
            beaconManager.addRangeNotifier(new RangeNotifier() {
                @Override
                public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                    Set<Integer> drones_changed = new HashSet<Integer>();

                    for (Beacon b : beacons) {
                        int result = DroneItemContent.addNewItem("Test", "Testdrone", b.getIdentifier(1).toString(), b.getRssi(), Color.BLUE);
                        if(result >= 0)
                            drones_changed.add(result);
                    }

                    for (int i : drones_changed) {
                        adapter.notifyItemChanged(i);
                    }

                }
            });

            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(adapter);

            beaconManager.setForegroundScanPeriod(500); // in ms
            beaconManager.setForegroundBetweenScanPeriod(100);
            beaconManager.startRangingBeacons(new Region("Regiontest", null, null, null));

        }






        return view;
    }
}