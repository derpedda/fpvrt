package net.pedda.fpvracetimer.ui.races;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.FragmentNavigatorDestinationBuilder;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.pedda.fpvracetimer.R;
import net.pedda.fpvracetimer.db.Race;

import java.util.ArrayList;
import java.util.List;

public class RacesFragment extends Fragment {

    private RacesViewModel mViewModel;

    private RacesRecyclerAdapter mAdapter;
    private RecyclerView mRecycler;

    public static RacesFragment newInstance() {
        return new RacesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_races, container, false);
        mRecycler = root.findViewById(R.id.races_recyclerview);

        FloatingActionButton fab = root.findViewById(R.id.races_fab);

        fab.setOnClickListener((sender) -> {
            NewRaceDialog nrd = new NewRaceDialog(requireActivity(), "Create race");
            nrd.show(requireActivity().getSupportFragmentManager(), "CreateRaceDialog");
        });

        mAdapter = new RacesRecyclerAdapter((Race r, int pos) -> {
            // on details selected
            navigateToDetailFragment(r);
        });

        mViewModel.getRaces().observe(getViewLifecycleOwner(), (races) -> {
            mAdapter.setDataSet(races);
            mAdapter.notifyDataSetChanged();
        });

        mViewModel.setAdapter(mAdapter);

        mRecycler.setAdapter(mAdapter);

        return root;
    }

    private void navigateToDetailFragment(Race r) {

        RaceDetailFragment rdf = new RaceDetailFragment();

        FragmentManager fragmentManager = getParentFragmentManager();

        NavController navController = NavHostFragment.findNavController(this);
        Bundle bundle = new Bundle();
        bundle.putInt("raceid", r.getUid());
        navController.navigate(R.id.action_racesFragment_to_raceDetailFragment, bundle);

//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.nav_host_fragment_activity_main, rdf);
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        transaction.commit();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RacesViewModel.class);

    }

}