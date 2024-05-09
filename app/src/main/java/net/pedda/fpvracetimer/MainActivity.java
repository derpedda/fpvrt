package net.pedda.fpvracetimer;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ThemedSpinnerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import net.pedda.fpvracetimer.camtools.HelperFunctionsCV;
import net.pedda.fpvracetimer.databinding.ActivityMainBinding;
import net.pedda.fpvracetimer.db.DBUtils;

import org.opencv.android.OpenCVLoader;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int NR_CORES = Runtime.getRuntime().availableProcessors();
    ExecutorService executorService = Executors.newFixedThreadPool(NR_CORES);
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            NR_CORES, // initial Size
            NR_CORES, // Max Size
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            workQueue
    );

    private ActivityMainBinding binding;

    public ThreadPoolExecutor getPoolExecutor() {
        return threadPoolExecutor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (OpenCVLoader.initLocal()) {
            Log.i("FPVRace", "OpenCV loaded successfully");
//            (Toast.makeText(this, "OpenCV initialization successfull!", Toast.LENGTH_LONG)).show();
        } else {
            Log.e("FPVRace", "OpenCV initialization failed!");
//            (Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG)).show();
        }

        DBUtils.init(this);
        HelperFunctionsCV.init();
    }

}