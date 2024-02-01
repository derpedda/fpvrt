package net.pedda.fpvracetimer.ui.droneconfig;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.pedda.fpvracetimer.R;
import net.pedda.fpvracetimer.ble.BLETool;
import net.pedda.fpvracetimer.ble.BluetoothLeConnectionService;

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
    private static final String TAG = "DroneFragment";
    private int mColumnCount = 1;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";


    private BeaconManager beaconManager;
    private Region region;
    private BluetoothLeConnectionService bluetoothService;


    // ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });


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
    public void onResume() {
        Activity ac = requireActivity();
        ContextCompat.registerReceiver(ac, gattUpdateReceiver, makeGattUpdateIntentFilter(), ContextCompat.RECEIVER_EXPORTED);
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(gattUpdateReceiver);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeConnectionService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeConnectionService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeConnectionService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(MyDroneRecyclerViewAdapter.ACTION_SETCOLOR);
        return intentFilter;
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothService = ((BluetoothLeConnectionService.LocalBinder) service).getService();
            if (bluetoothService != null) {
                if (!bluetoothService.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth");
                }
                // perform device connection
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothService = null;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        Intent gattServiceIntent = new Intent(requireContext(), BluetoothLeConnectionService.class);
        requireActivity().bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.i(TAG, "onReceive: received intent");
            if (BluetoothLeConnectionService.ACTION_GATT_CONNECTED.equals(action)) {
//                connected = true;
                // TODO: Add handling

            } else if (BluetoothLeConnectionService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                connected = false;
            } else if (BluetoothLeConnectionService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
//                displayGattServices(bluetoothService.getSupportedGattServices());
                // continue updating, inform the service
                bluetoothService.writeUpdates();
                Log.i(TAG, "onReceive: " + bluetoothService.getSupportedGattServices().size());

            } else if (MyDroneRecyclerViewAdapter.ACTION_SETCOLOR.equals(action)) {
                Log.i(TAG, "onReceive: Starting updating the color");
                bluetoothService.updateColorOnDrone(intent.getStringExtra("MAC"), intent.getIntExtra("COLOR", 0));
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drone_list, container, false);

        if (BLETool.check_blepermission(this.requireContext())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.drones_actionmenu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // TODO: Make this refresh the scan
                beaconManager.startRangingBeacons(region);
                return true;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.STARTED);


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
                    beaconManager.stopRangingBeacons(region);

                    for (Beacon b : beacons) {
                        int result = DroneItemContent.addNewItem("Test", b.getBluetoothAddress(), "Testdrone", b.getId2().toString(), b.getRssi(), null);
                        if (result >= 0)
                            drones_changed.add(result);
                    }

                    for (int i : drones_changed) {
                        adapter.notifyItemChanged(i);
                    }

//                    beaconManager.startRangingBeacons(region);

                }
            });

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_dronelist);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(adapter);

            beaconManager.setForegroundScanPeriod(1000); // in ms
            beaconManager.setForegroundBetweenScanPeriod(500);
            region = new Region("Regiontest", null, null, null);
            beaconManager.startRangingBeacons(region);

        }
        return view;
    }
}