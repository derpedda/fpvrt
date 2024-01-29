package net.pedda.fpvracetimer.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Drone.class, RecordedRecord.class}, version = 1)
public abstract class FPVDb extends RoomDatabase {
    public abstract DroneDao droneDao();

    public abstract RecordedRecordDao recordedRecordDao();

    private static volatile FPVDb fpvDb;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static FPVDb getDatabase(final Context context) {

        if (fpvDb == null) {
            if(context == null) {
                return null;
            }
            synchronized (FPVDb.class) {
                if (fpvDb == null) {
                    fpvDb = Room.databaseBuilder(context.getApplicationContext(), FPVDb.class, "fpvdb")
                            .build();
                }
            }
        }
        return fpvDb;
    }

}
