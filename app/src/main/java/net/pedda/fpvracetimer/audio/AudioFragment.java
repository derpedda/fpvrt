package net.pedda.fpvracetimer.audio;

import androidx.annotation.MenuRes;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.storage.StorageManager;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.pedda.fpvracetimer.MainActivity;
import net.pedda.fpvracetimer.R;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Executor;

public class AudioFragment extends Fragment {

    private AudioViewModel mViewModel;

    private Executor mExecutor;
    private Button btn_record;

    private Button btn_uploadall;
    private TextView tv_recording;

    private MediaRecorder mr;
    private boolean isRecording = false;

    private File current_recording = null;

    public static AudioFragment newInstance() {
        return new AudioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_audio, container, false);

        mExecutor = ((MainActivity)requireActivity()).getPoolExecutor();

        btn_record = root.findViewById(R.id.btn_recordcontrol);

        btn_record.setOnClickListener(v -> {
            if(isRecording) {
                // already running, stop it
                mr.stop();
                mr.release();
                isRecording = false;
                UploadTask ut = new UploadTask(
                        requireContext().getString(R.string.s3_bucket),
                        requireContext().getString(R.string.s3_endpoint),
                        requireContext().getString(R.string.s3_access),
                        requireContext().getString(R.string.s3_secret),
                        current_recording
                        );
                mExecutor.execute(ut);
                current_recording = null;
                btn_record.setText("Start recording");

            } else {
                // not running, start it
                mr = null;
                mr = new MediaRecorder();
                mr.setAudioSource(MediaRecorder.AudioSource.UNPROCESSED);
                String filename = "record_"+ System.currentTimeMillis() +".mp4";
                current_recording = new File(requireContext().getCacheDir(), filename);
                mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mr.setOutputFile(current_recording);
                mr.setAudioSamplingRate(48000);
                mr.setAudioEncodingBitRate(384000);
                mr.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
                try {
                    mr.prepare();
                    mr.start();
                    isRecording = true;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                btn_record.setText("Stop recording");

            }
        });

        btn_uploadall = root.findViewById(R.id.btn_uploadall);
        btn_uploadall.setOnClickListener((btn) -> {
            File[] files = requireContext().getCacheDir().listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".aac");
                }
            });
            for (File f : files) {
                UploadTaskExists ut = new UploadTaskExists(requireContext().getString(R.string.s3_bucket),
                        requireContext().getString(R.string.s3_endpoint),
                        requireContext().getString(R.string.s3_access),
                        requireContext().getString(R.string.s3_secret),
                        f);
                mExecutor.execute(ut);
            }

        });

        mr = new MediaRecorder();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AudioViewModel.class);
        // TODO: Use the ViewModel
    }



}