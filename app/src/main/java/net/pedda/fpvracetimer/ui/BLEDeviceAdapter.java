package net.pedda.fpvracetimer.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.pedda.fpvracetimer.R;
import net.pedda.fpvracetimer.models.BLEObjectModel;

import java.util.List;

public class BLEDeviceAdapter extends RecyclerView.Adapter<BLEDeviceAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView UUIDTextView;
        public TextView RSSITextView;
        public TextView TypeTextView;
        public TextView InstanceTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            UUIDTextView = (TextView) itemView.findViewById(R.id.bledevice_uuid);
            RSSITextView = (TextView) itemView.findViewById(R.id.bledevice_rssi);
            TypeTextView = (TextView) itemView.findViewById(R.id.bledevice_type);
            InstanceTextView = (TextView) itemView.findViewById(R.id.bledevice_instance);

        }

    }

    private List<BLEObjectModel> mDevices;

    public BLEDeviceAdapter(List<BLEObjectModel> devices) {
        mDevices = devices;
    }

    @NonNull
    @Override
    public BLEDeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);

        View deviceView = inflater.inflate(R.layout.item_bledevice, parent, false);
        ViewHolder viewHolder = new ViewHolder(deviceView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BLEDeviceAdapter.ViewHolder holder, int position) {
        BLEObjectModel device = mDevices.get(position);

        TextView UUIDTextView = holder.UUIDTextView;
        UUIDTextView.setText(device.getUuid());
        TextView RSSITextView = holder.RSSITextView;
        RSSITextView.setText(device.getRssi());
        TextView TypeTextView = holder.TypeTextView;
        TypeTextView.setText(device.getType());
        TextView InstanceTextView = holder.InstanceTextView;
        InstanceTextView.setText(device.getInstance());
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }
}
