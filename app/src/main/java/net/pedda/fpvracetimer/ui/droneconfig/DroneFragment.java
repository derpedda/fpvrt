package net.pedda.fpvracetimer.ui.droneconfig;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import net.pedda.fpvracetimer.R;
import net.pedda.fpvracetimer.ble.BluetoothLeConnectionService;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 */
public class DroneFragment extends Fragment implements MyDroneRecyclerViewAdapter.SingleSurveyRequestedListener {

    // TODO: Customize parameters
    private static final String TAG = "DroneFragment";

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";


    private BeaconManager beaconManager;
    private Region region;
    private BluetoothLeConnectionService bluetoothService;

    private DroneViewModel dvm;

    private RecyclerView recyclerView;
    private MyDroneRecyclerViewAdapter adapter;


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
        intentFilter.addAction(MyDroneRecyclerViewAdapter.ACTION_SETNAME);
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

        Intent gattServiceIntent = new Intent(requireContext(), BluetoothLeConnectionService.class);
        requireActivity().bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        dvm = new ViewModelProvider(this).get(DroneViewModel.class);
        dvm.getDrones().observe(this, drones -> {
            adapter.setValues(drones);
            adapter.notifyDataSetChanged(); // jaja
        });

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
                Log.i(TAG, "onReceive: Start updating the color");
                bluetoothService.updateColorOnDrone(intent.getStringExtra("MAC"), intent.getIntExtra("COLOR", 0));
            } else if (MyDroneRecyclerViewAdapter.ACTION_SETNAME.equals(action)) {
                Log.i(TAG, "onReceive: Start updating the name");
                bluetoothService.updateNameOnDrone(intent.getStringExtra("MAC"), intent.getStringExtra("NAME"));
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drone_list, container, false);

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

            adapter = new MyDroneRecyclerViewAdapter(new ArrayList<>(), (AppCompatActivity) requireActivity());

            beaconManager = BeaconManager.getInstanceForApplication(this.requireContext());
            beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
            beaconManager.removeAllRangeNotifiers();
            beaconManager.addRangeNotifier(dvm.rnOnce(beaconManager, adapter));
            adapter.setSurveyRequestedListener(this);

            recyclerView = view.findViewById(R.id.recyclerview_dronelist);
            recyclerView.setAdapter(adapter);

            beaconManager.setForegroundScanPeriod(1000); // in ms
            beaconManager.setForegroundBetweenScanPeriod(500);
            region = new Region("Region", null, null, null);
            dvm.startBLEScan(beaconManager, region);

        }
        return view;
    }

    @Override
    public void SingleSurveyRequested(MyDroneRecyclerViewAdapter adapter) {
        dvm.startBLEScan(beaconManager, region);
    }
}