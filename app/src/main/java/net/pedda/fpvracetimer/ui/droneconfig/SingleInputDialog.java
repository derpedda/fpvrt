package net.pedda.fpvracetimer.ui.droneconfig;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import net.pedda.fpvracetimer.R;
import net.pedda.fpvracetimer.db.DBUtils;
import net.pedda.fpvracetimer.db.Drone;

public class SingleInputDialog extends DialogFragment {

    private AlertDialog.Builder mBuilder;

    private String mTitle;

    private String mValue = "";

    protected SingleInputDialog(@NonNull Context context, String title) {
        super();
        mTitle = title;
        mBuilder = new AlertDialog.Builder(context);
    }

    public interface InputDialogListener {
        public void onDialogPositiveClick(SingleInputDialog dialog);
        public void onDialogNegativeClick(SingleInputDialog dialog);
    }

    InputDialogListener listener;


    private void setupDialog() {
        mBuilder.setTitle(mTitle);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.dialog_singleinput, null);
        EditText tInput = content.findViewById(R.id.dronenamedialog_name);
        mBuilder.setView(content);
        mBuilder.setPositiveButton("Save", (dialog, id) -> {
            if(listener != null) {
                mValue = String.valueOf(tInput.getText());
                listener.onDialogPositiveClick(this);
            }
        });
        mBuilder.setNegativeButton("Cancel", (dialog, id) -> {
            if(listener != null) {
                listener.onDialogNegativeClick(this);
            } else {
                dialog.dismiss();
            }
        });
    }

    public String getValue() {
        return mValue;
    }

    public void setButtonListener(InputDialogListener idl) {
        listener = idl;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setupDialog();
        return mBuilder.create();
    }
}
