package net.pedda.fpvracetimer.ui.notifications;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.pedda.fpvracetimer.R;
import net.pedda.fpvracetimer.databinding.FragmentNotificationsBinding;
import net.pedda.fpvracetimer.models.BLEObjectModel;
import net.pedda.fpvracetimer.ui.BLEDeviceAdapter;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.opencv.core.Range;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NotificationsFragment extends Fragment {


    public static final int RC_LOCATION_FINE = 1;
    public static final int RC_LOCATION_COARSE = 2;
    public static final int RC_BLUETOOTH = 3;

    private BeaconManager beaconManager;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RC_LOCATION_FINE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(this.getContext(), "Location Fine Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getContext(), "Location Fine Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (requestCode == RC_LOCATION_COARSE) {
                // Checking whether user granted the permission or not.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Showing the toast message
                    Toast.makeText(this.getContext(), "Location Coarse Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this.getContext(), "Location Coarse Permission Denied", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == RC_BLUETOOTH) {
                // Checking whether user granted the permission or not.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Showing the toast message
                    Toast.makeText(this.getContext(), "Location Bluetooth Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this.getContext(), "Location Bluetooth Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, ask for it
            ActivityCompat.requestPermissions(this.getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION},RC_LOCATION_FINE);
        }

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, ask for it
            ActivityCompat.requestPermissions(this.getActivity(), new String[] {Manifest.permission.BLUETOOTH_SCAN},RC_BLUETOOTH);
        }

        List<BLEObjectModel> devices = BLEObjectModel.genList();
        BLEDeviceAdapter adapter = new BLEDeviceAdapter(devices);


        beaconManager = BeaconManager.getInstanceForApplication(this.getContext());
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                devices.clear();


                for (Iterator<Beacon> it = beacons.iterator(); it.hasNext(); ) {
                    Beacon b = it.next();
                    devices.add(new BLEObjectModel(String.valueOf(b.getServiceUuid()),String.valueOf(b.getRssi()), b.getIdentifier(0).toString(), b.getIdentifier(1).toString()));
                }
                adapter.notifyDataSetChanged();

            }
        });


        RecyclerView rvBLE = binding.bleRecyclerciew;
        rvBLE.setAdapter(adapter);
        rvBLE.setLayoutManager(new LinearLayoutManager(this.getContext()));

        beaconManager.setForegroundScanPeriod(500); // in ms
        beaconManager.setForegroundBetweenScanPeriod(100);
        beaconManager.startRangingBeacons(new Region("Regiontest", null, null, null));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}