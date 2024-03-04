package net.pedda.fpvracetimer.ui.droneconfig;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;


import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.tianscar.colorview.ColorView;

import net.pedda.fpvracetimer.R;
import net.pedda.fpvracetimer.ble.BLETool;
import net.pedda.fpvracetimer.ble.BluetoothLeConnectionService;
import net.pedda.fpvracetimer.databinding.FragmentDroneBinding;
import net.pedda.fpvracetimer.db.DBUtils;
import net.pedda.fpvracetimer.db.Drone;
import net.pedda.fpvracetimer.db.DroneDao;
import net.pedda.fpvracetimer.db.FPVDb;
import net.pedda.fpvracetimer.ui.droneconfig.DroneItemContent.DroneItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DroneItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDroneRecyclerViewAdapter extends RecyclerView.Adapter<MyDroneRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "DroneRecyclerViewAdapter";

    private List<Drone> mValues;

    private AppCompatActivity mActivity;

    public static final String ACTION_SETCOLOR = "net.pedda.fpvracetimer.drone.setcolor";
    public static final String ACTION_SETNAME = "net.pedda.fpvracetimer.drone.setname";

    public MyDroneRecyclerViewAdapter(List<Drone> items, AppCompatActivity act) {
        super();
        mValues = items;
        mActivity = act;
    }

    public void setValues(List<Drone> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    return new ViewHolder(FragmentDroneBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }


    public interface SingleSurveyRequestedListener {
        public void SingleSurveyRequested(MyDroneRecyclerViewAdapter adapter);
    }

    private SingleSurveyRequestedListener mSurveyRequestedListener;


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int position_int = holder.getBindingAdapterPosition();
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.valueOf(mValues.get(position).getTransponderid()));
        holder.mNameView.setText(mValues.get(position).getDronename());
        holder.mColorView.setColor(mValues.get(position).getColorI());
        holder.mRSSIView.setText(String.valueOf(mValues.get(position).getRssi()));
        long now = System.currentTimeMillis();
        Color bgcol = (((now - 2000) - holder.mItem.getLastSeen()) > 0) ? Color.valueOf(0.6f,0.6f,0.6f) : Color.valueOf(1,1,1);
        holder.itemView.setBackgroundColor(bgcol.toArgb());

        holder.mNameView.setOnClickListener((view) -> {
            SingleInputDialog dialog = new SingleInputDialog(mActivity, "Edit name");
            dialog.setButtonListener(new SingleInputDialog.InputDialogListener() {
                @Override
                public void onDialogPositiveClick(SingleInputDialog dialog) {
                    holder.mItem.setDronename(dialog.getValue());
                    DBUtils.updateDrone(holder.mItem);
                    if(mSurveyRequestedListener != null) {
                        mSurveyRequestedListener.SingleSurveyRequested((MyDroneRecyclerViewAdapter) holder.getBindingAdapter());
                    }
                    Intent intent = new Intent(ACTION_SETNAME);
                    intent.putExtra("MAC", holder.mItem.getMac());
                    intent.putExtra("NAME", holder.mItem.getDronename());
                    view.getContext().sendBroadcast(intent);
                }

                @Override
                public void onDialogNegativeClick(SingleInputDialog dialog) {
                    dialog.dismissAllowingStateLoss();
                }
            });
            dialog.show(mActivity.getSupportFragmentManager(), "ChangeNameDialog");

        });

        String[] COLORS = {
                "#FF0000", "#0000FF", "#00FF00",
                "#FF7F00","#FFFF00","#FF00FF",
                "#00FFFF"
        };

        holder.mColorView.setOnClickListener(v -> new MaterialColorPickerDialog
                .Builder(v.getContext())
                .setTitle("Pick a color")
                .setColorShape(ColorShape.SQAURE)
                .setColors(COLORS)
                .setDefaultColor(mValues.get(position_int).getColorI())
                .setColorListener((color, colorHex) -> {
                    // Handle Color Selection
                    Drone d = mValues.get(holder.getBindingAdapterPosition());
                    d.setColorI(color);
                    Intent intent = new Intent(ACTION_SETCOLOR);
                    intent.putExtra("MAC", d.getMac());
                    intent.putExtra("COLOR", d.getColorI());
                    DBUtils.updateDrone(d);
                    v.getContext().sendBroadcast(intent);
//                    LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(intent);
                    holder.getBindingAdapter().notifyItemChanged(holder.getBindingAdapterPosition());

                })
                .show());

    }

    public void setSurveyRequestedListener(SingleSurveyRequestedListener listener){
        mSurveyRequestedListener = listener;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mNameView;

        public final ColorView mColorView;

        public final TextView mRSSIView;
        public Drone mItem;

    public ViewHolder(FragmentDroneBinding binding) {
      super(binding.getRoot());
      mIdView = binding.dcItemNumber;
      mNameView = binding.dcDronename;
      mColorView = binding.dcDronecolor;
      mRSSIView = binding.dcDronerssi;

    }

    @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}