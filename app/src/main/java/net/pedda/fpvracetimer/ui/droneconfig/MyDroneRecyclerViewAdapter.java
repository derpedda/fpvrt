package net.pedda.fpvracetimer.ui.droneconfig;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.github.dhaval2404.colorpicker.model.ColorSwatch;
import com.tianscar.colorview.ColorView;

import net.pedda.fpvracetimer.databinding.FragmentDroneBinding;
import net.pedda.fpvracetimer.ui.droneconfig.DroneItemContent.DroneItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DroneItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDroneRecyclerViewAdapter extends RecyclerView.Adapter<MyDroneRecyclerViewAdapter.ViewHolder> {

    private final List<DroneItem> mValues;

    public MyDroneRecyclerViewAdapter(List<DroneItem> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    return new ViewHolder(FragmentDroneBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int position_int = holder.getBindingAdapterPosition();
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mNameView.setText(mValues.get(position).droneName);
        holder.mColorView.setColor(mValues.get(position).dColor);
        holder.mRSSIView.setText(String.valueOf(mValues.get(position).dRSSI));

        holder.mColorView.setOnClickListener(v -> new MaterialColorPickerDialog
                .Builder(v.getContext())
                .setTitle("Pick a color")
                .setColorShape(ColorShape.SQAURE)
                .setColorSwatch(ColorSwatch._300)
                .setDefaultColor(mValues.get(position_int).dColor)
                .setColorListener(new ColorListener() {
                    @Override
                    public void onColorSelected(int color, @NotNull String colorHex) {
                        // Handle Color Selection
                        mValues.get(holder.getBindingAdapterPosition()).dColor = color;
                        holder.getBindingAdapter().notifyItemChanged(holder.getBindingAdapterPosition());
                    }
                })
                .show());

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
        public DroneItem mItem;

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