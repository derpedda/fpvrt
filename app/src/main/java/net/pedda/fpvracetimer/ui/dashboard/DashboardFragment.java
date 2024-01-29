package net.pedda.fpvracetimer.ui.dashboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import net.pedda.fpvracetimer.R;
import net.pedda.fpvracetimer.databinding.FragmentDashboardBinding;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class DashboardFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {

    private FragmentDashboardBinding binding;
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat mRgba;

    private final static int CAMERA_PERMISSION_CODE = 12;

    // TODO: Fix this
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
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        if (OpenCVLoader.initLocal()) {
            Log.i("FPVRace", "OpenCV loaded successfully");
            (Toast.makeText(this.getContext(), "OpenCV initialization successfull!", Toast.LENGTH_LONG)).show();
        } else {
            Log.e("FPVRace", "OpenCV initialization failed!");
            (Toast.makeText(this.getContext(), "OpenCV initialization failed!", Toast.LENGTH_LONG)).show();
        }

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, ask for it
            ActivityCompat.requestPermissions(this.getActivity(), new String[] {Manifest.permission.CAMERA},12);
        }

        mOpenCvCameraView = (CameraBridgeViewBase) root.findViewById(R.id.fpvracetimer_camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraPermissionGranted();
        mOpenCvCameraView.enableView();


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
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        return mRgba;
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