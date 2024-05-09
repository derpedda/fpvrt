package net.pedda.fpvracetimer.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Dao
public interface DroneDao {
    @Insert
    void insertAll(Drone... drones);

    @Insert
    void insert(Drone d);

    @Delete
    void delete(Drone drone);

    @Query("SELECT * FROM drones")
    LiveData<List<Drone>> getAllObservable();

    @Query("SELECT * FROM drones;")
    List<Drone> getAll();


    @Query("SELECT * FROM drones WHERE transponderid= :tid")
    Drone getDrone(long tid);

    @Query("SELECT * FROM drones WHERE mac= :mac LIMIT 1;")
    Drone getDroneByMac(String mac);


    @Query(
            "SELECT * FROM drones JOIN recordedrecords on drones.transponderid=recordedrecords.droneId"
    )
    Map<Drone, List<RecordedRecord>> getAllRecords();


    @Transaction
    @Query(
            "SELECT * FROM drones" +
            " JOIN recordedrecords on drones.transponderid = recordedrecords.droneId" +
            " WHERE isFirst != 1 AND raceId == :raceid"
    )
    Map<Drone, List<RecordedRecord>> getAllValidRecordsForRace(long raceid);

    @Transaction
    @Query(
            "SELECT * FROM drones" +
                    " JOIN recordedrecords on drones.transponderid = recordedrecords.droneId"
    )
    Map<Drone, List<RecordedRecord>> getAllRecordsMap();


    @Query(
            "SELECT * FROM drones JOIN recordedrecords on droneId=transponderid where :transponderid"
    )
    Map<Drone, List<RecordedRecord>> getRecordsForDrone(long transponderid);

//    @Query(
//            "SELECT * FROM drones JOIN recordedrecords on drones.transponderid=recordedrecords.droneId WHERE isFirst = 0"
//    )
//    Map<Drone, List<RecordedRecordValid>> getAllValidRecords();


//    @Query(
//            "SELECT * FROM drones JOIN recordedrecords on droneId=transponderid where :transponderid AND isFirst = 0"
//    )
//    Map<Drone, List<RecordedRecordValid>> getValidRecordsForDrone(long transponderid);


    @Update
    void updateDrone(Drone d);

}