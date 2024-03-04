package net.pedda.fpvracetimer.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import net.pedda.fpvracetimer.R;
import net.pedda.fpvracetimer.ble.BLETool;
import net.pedda.fpvracetimer.camtools.HelperFunctions;
import net.pedda.fpvracetimer.databinding.FragmentHomeBinding;
import net.pedda.fpvracetimer.db.DBUtils;
import net.pedda.fpvracetimer.db.Drone;
import net.pedda.fpvracetimer.db.DroneDao;
import net.pedda.fpvracetimer.db.FPVDb;
import net.pedda.fpvracetimer.webtools.FPVWebServer;
import net.pedda.fpvracetimer.webtools.WebserverWorker;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    FPVDb fpvDB;

    static final HashMap<String, String> permissions = new HashMap<>();
    static {
        permissions.put(Manifest.permission.BLUETOOTH_CONNECT, "Bluetooth Connect");
        permissions.put(Manifest.permission.BLUETOOTH_SCAN, "Bluetooth Scan");
        permissions.put(Manifest.permission.BLUETOOTH, "Bluetooth");
        permissions.put(Manifest.permission.ACCESS_COARSE_LOCATION, "Access Coarse Location");
        permissions.put(Manifest.permission.ACCESS_FINE_LOCATION, "Access Fine Location");
        permissions.put(Manifest.permission.CAMERA, "Camera");
        permissions.put(Manifest.permission.RECORD_AUDIO, "Record Audio");
        permissions.put(Manifest.permission.INTERNET, "Internet");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        final Button btnStartWeb = binding.btnControlwebserver;
//        btnStartWeb.setEnabled(!FPVWebServer.isRunning());
        btnStartWeb.setEnabled(false);
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        btnStartWeb.setOnClickListener(v -> {
            WorkRequest webWork = new OneTimeWorkRequest.Builder(WebserverWorker.class).build();
            WorkManager.getInstance(getContext().getApplicationContext()).enqueue(webWork);
            btnStartWeb.setEnabled(false);
        });

        requestAllPermissionsNeeded(requireContext());

        Button btnClearDb = root.findViewById(R.id.btn_cleardb);
        btnClearDb.setEnabled(true);
        btnClearDb.setOnClickListener((view) -> {
            DBUtils.clearDB(requireContext());
        });


        return root;
    }

    // ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                for(String k : isGranted.keySet()) {
                    if (isGranted.get(k)) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // feature requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                        final String msg = String.format("The permission %s is needed for this App to function, disabling the functions needed.", permissions.getOrDefault(k, "NOT_FOUND"));
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                }

            });

    private void requestAllPermissionsNeeded(Context ctx) {

        for(String p : permissions.keySet()) {
            List<String> permissionsToAskFor = new ArrayList<>(permissions.size());
            if (ctx.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                // not granted yet
                permissionsToAskFor.add(p);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissionLauncher.launch(permissions.keySet().toArray(new String[0]));
            } else {
                // TODO: make this work for older APIs earlier than S
                Toast.makeText(requireContext(), "Support coming soon, stay tuned", Toast.LENGTH_LONG).show();
            }
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}