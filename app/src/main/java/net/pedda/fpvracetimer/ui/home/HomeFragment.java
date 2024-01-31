package net.pedda.fpvracetimer.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import net.pedda.fpvracetimer.databinding.FragmentHomeBinding;
import net.pedda.fpvracetimer.db.Drone;
import net.pedda.fpvracetimer.db.DroneDao;
import net.pedda.fpvracetimer.db.FPVDb;
import net.pedda.fpvracetimer.webtools.FPVWebServer;
import net.pedda.fpvracetimer.webtools.WebserverWorker;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    FPVDb fpvDB;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        final Button btnStartWeb = binding.btnControlwebserver;
        btnStartWeb.setEnabled(!FPVWebServer.isRunning());
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        btnStartWeb.setOnClickListener(v -> {
            WorkRequest webWork = new OneTimeWorkRequest.Builder(WebserverWorker.class).build();
            WorkManager.getInstance(getContext().getApplicationContext()).enqueue(webWork);
            btnStartWeb.setEnabled(false);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}