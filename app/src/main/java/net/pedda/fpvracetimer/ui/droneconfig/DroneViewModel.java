package net.pedda.fpvracetimer.ui.droneconfig;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.pedda.fpvracetimer.db.FPVDb;

import java.util.List;

public class DroneViewModel extends ViewModel {

    private final MutableLiveData<List<DroneItemContent.DroneItem>> mDroneItems;


    public DroneViewModel() {
        FPVDb db = FPVDb.getDatabase(null);
        this.mDroneItems = new MutableLiveData<>();

    }

    public LiveData<List<DroneItemContent.DroneItem>> getDroneItem() {
        return mDroneItems;
    }

}
