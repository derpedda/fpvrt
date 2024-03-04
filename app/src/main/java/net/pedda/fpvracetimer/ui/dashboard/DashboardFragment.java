package net.pedda.fpvracetimer.ui.dashboard;

import static org.opencv.core.CvType.CV_8UC4;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;

import net.pedda.fpvracetimer.R;
import net.pedda.fpvracetimer.camtools.HelperFunctions;
import net.pedda.fpvracetimer.camtools.HelperFunctionsCV;
import net.pedda.fpvracetimer.databinding.FragmentDashboardBinding;
import net.pedda.fpvracetimer.db.DBUtils;
import net.pedda.fpvracetimer.db.Drone;
import net.pedda.fpvracetimer.db.FPVDb;
import net.pedda.fpvracetimer.db.helperobjects.DroneDetectedEvent;
import net.pedda.fpvracetimer.ui.customviews.FPVJavaCamera2View;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {


    private static final String TAG = "FPVRT-DF";
    private FragmentDashboardBinding binding;
//    private CameraBridgeViewBase mOpenCvCameraView;
    private FPVJavaCamera2View mOpenCvCameraView;
    private Mat mRgba;

    private final static int CAMERA_PERMISSION_CODE = 12;

    private SeekBar mExposureSlider;
    private SeekBar mSensitivitySlider;
    private final static float mExposureFactor = 10;

    private DashboardViewModel dashboardViewModel;
    private FloatingActionButton mFabRaceControl;

    // TODO: Remove this and make sure it gets asked when opening the App
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(this.getContext(), "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getContext(), "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, ask for it
            ActivityCompat.requestPermissions(this.getActivity(), new String[] {Manifest.permission.CAMERA},12);
        }

        mOpenCvCameraView = root.findViewById(R.id.fpvracetimer_camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraPermissionGranted();
        mOpenCvCameraView.enableView();

        mExposureSlider = root.findViewById(R.id.cameraview_slider_exposure);
        mExposureSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    Log.d(TAG, "onProgressChanged: Progress changed");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int p = seekBar.getProgress();
                mOpenCvCameraView.setExposure((long) (p*DashboardFragment.mExposureFactor));

            }
        });

        mSensitivitySlider = root.findViewById(R.id.cameraview_slider_sensitivity);
        mSensitivitySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    Log.d(TAG, "onProgressChanged: Progress changed");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int p = seekBar.getProgress();
                mOpenCvCameraView.setSensitivity(p);

            }

        });


        mFabRaceControl = root.findViewById(R.id.racecontrol_fab);

        mFabRaceControl.setOnClickListener((View v) -> {
            dashboardViewModel.processRaceControlClick((FloatingActionButton)v);
        });

        dashboardViewModel.setMRaceChangedListener((race) -> {
            if(race != null) {

                mFabRaceControl.setImageResource(DashboardViewModel.Companion.getStateDrawable(race.getRaceState()));
            } else {
                mFabRaceControl.setImageResource(DashboardViewModel.Companion.getStateDrawable(DashboardViewModel.RaceState.ERROR));
            }
        });

        dashboardViewModel.getMRaceLive().observe(this.getViewLifecycleOwner(), dashboardViewModel::setMCurrentRace);



        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        binding = null;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CV_8UC4);
        Range<Long> er = mOpenCvCameraView.getExposureRange();
        mExposureSlider.setMin((int)(er.getLower()/DashboardFragment.mExposureFactor));
        mExposureSlider.setMax((int)(er.getUpper()/5000));

        Range<Integer> sr = mOpenCvCameraView.getSensitivityRange();
        mSensitivitySlider.setMin(sr.getLower());
        mSensitivitySlider.setMax(sr.getUpper());

    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        long timestamp = System.currentTimeMillis();
        // tap in here
        Mat red = HelperFunctionsCV.getColorMask(mRgba, HelperFunctionsCV.COLORRANGES.RED);
        // init OpenCVHelper first at least once...
        Mat temp = mRgba; //Mat.zeros(mRgba.size(), CV_8UC4);

        // Draw a line on the screen
        HelperFunctionsCV.drawVerticalLine(temp);
        HelperFunctionsCV.drawDetectionRectangle(temp);

        Mat detArea = HelperFunctionsCV.getDetectionSubarea(temp);
        detArea = detArea.clone();

        List<String> colors = HelperFunctionsCV.detectColors(detArea);
        String text_detcols;
        if(colors.size() > 0)
        {
            text_detcols = String.join("+", colors);
            List<Drone> drones = DBUtils.detectDrones(text_detcols);
//
//        // submit timing event to DB
            for (Drone d : drones) {
                dashboardViewModel.droneDetected(timestamp, d);
            }
        } else {
            text_detcols = "";
        }
        HelperFunctionsCV.drawColorText(temp, text_detcols);

        // TODO Next, detect drones nearby


        return temp;
    }

    @Override
    public void onResume() {
        super.onResume();
//        OpenCVLoader.initLocal();
//        if (!OpenCVLoader.initDebug()) {
//            Log.d("FPVRace", "Internal OpenCV library not found. Using OpenCVManager for init");
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this.getContext(), mLoaderCallback);
//        } else {
//            Log.d("FPVRace", "OpenCVLib found in package");
//            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//        }
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.enableView();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}