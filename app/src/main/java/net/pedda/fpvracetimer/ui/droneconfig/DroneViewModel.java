package net.pedda.fpvracetimer.ui.droneconfig;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import net.pedda.fpvracetimer.db.DBUtils;
import net.pedda.fpvracetimer.db.Drone;
import net.pedda.fpvracetimer.db.DroneDao;
import net.pedda.fpvracetimer.db.FPVDb;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.List;

public class DroneViewModel extends ViewModel {

    private final LiveData<List<Drone>> mDrones;

    private final FPVDb fpvDb;

    public LiveData<List<Drone>> getDrones() {
        return mDrones;
    }

    public DroneViewModel() {
        fpvDb = FPVDb.getDatabase(null);
        assert fpvDb != null;
        DroneDao dd = fpvDb.droneDao();
        this.mDrones = dd.getAllObservable();
    }

    public void addNewDrone(String deviceMac, String droneName, String droneId, int dRSSI, Integer dColor) {
        Drone d = new Drone(Long.decode(droneId));
//        d.setTransponderid(Long.decode(droneId));
        d.setDronename(droneName);
        d.setRssi(dRSSI);
        d.setMac(deviceMac);
        d.setLastSeen(System.currentTimeMillis());
        DBUtils.submitDroneToDB(d);

    }

    public void startBLEScan(BeaconManager bm, Region region){
        bm.startRangingBeacons(region);
    }

    public RangeNotifier rnOnce(BeaconManager bm, RecyclerView.Adapter<MyDroneRecyclerViewAdapter.ViewHolder> adapter) {
        return (beacons, region) -> {
            bm.stopRangingBeacons(region);
            bm.removeAllRangeNotifiers();
            for (Beacon b : beacons) {
                // TODO: fix name
                addNewDrone(b.getBluetoothAddress(),"Testdrone", b.getId2().toString(), b.getRssi(), null);
            }
            adapter.notifyDataSetChanged();

        };
    }
}
