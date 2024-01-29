package net.pedda.fpvracetimer.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface DroneDao {
    @Insert
    void insertAll(Drone... drones);

    @Delete
    void delete(Drone drone);

    @Query("SELECT * FROM drones")
    List<Drone> getAll();

    @Query("SELECT * FROM drones WHERE uid= :duid")
    Drone getDrone(int duid);

    @Transaction
    @Query("SELECT * FROM drones")
    List<DroneWithRecords> getAllRecords();

}