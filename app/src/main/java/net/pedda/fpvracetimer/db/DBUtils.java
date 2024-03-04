package net.pedda.fpvracetimer.db;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import net.pedda.fpvracetimer.MainActivity;
import net.pedda.fpvracetimer.camtools.HelperFunctionsCV;
import net.pedda.fpvracetimer.db.helperobjects.DroneDetectedEvent;
import net.pedda.fpvracetimer.helperclasses.NoActiveRaceException;
import net.pedda.fpvracetimer.helperclasses.Result;
import net.pedda.fpvracetimer.helperclasses.ResultRaceDetails;
import net.pedda.fpvracetimer.helperclasses.ResultRaceDetailsAvailable;
import net.pedda.fpvracetimer.ui.droneconfig.DroneItemContent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class DBUtils {

    private static Executor mExecutor;

    public static final long DETETECTION_BLACKOUT_TIME = 1000; // in ms
    private static FPVDb mDb;
    private static DroneDao mDroneDao;
    private static RecordedRecordDao mRecordedRecordDao;

    private static HashMap<String, Drone> mDronesByColor = new HashMap<>();

    private static HashMap<String, Long> mDroneBlackout = new HashMap<>();


    private static void initDroneMaps() {
        List<Drone> drones = mDroneDao.getAll();
        initDroneMaps(drones);
    }

    private static void initDroneMaps(List<Drone> drones) {
        for (Drone d : Objects.requireNonNull(drones)) {
            mDronesByColor.put(d.getColor(), d);
        }
    }

    public static Result<Boolean> syncInit(Context ctx) {
        mDb = FPVDb.getDatabase(ctx);
        if (mDb != null) {
            mDroneDao = mDb.droneDao();
            mRecordedRecordDao = mDb.recordedRecordDao();

            LiveData<List<Drone>> dronesLive = mDb.droneDao().getAllObservable();
            initDroneMaps();
            dronesLive.observe((AppCompatActivity) ctx, (data) -> {
                Log.d("DBUtils", "syncInit: Test");
            });

        }
        return new Result.Success<>(true);
    }

    public static void init(Context ctx) {
        mExecutor = ((MainActivity) ctx).getPoolExecutor();
        mExecutor.execute(() -> {
            try {
                syncInit(ctx);
            } catch (Exception e) {
                Result<Void> errorResult = new Result.Error<>(e);
            }

        });

    }


    private static boolean isInitialized() {
        return mDb != null;
    }

    public static List<Drone> detectDrones(String color) {
        return detectDrones(color, System.currentTimeMillis());
    }

    public static List<Drone> detectDrones(String color, long timestamp) {
        if (!isInitialized())
            return null;

        LinkedList<Drone> drones = new LinkedList<>();

        if (color.length() == 0)
            return drones;

        String[] colors = color.split("\\+");
        if (colors.length > 1) {
            // we have more than one, difficult... but well, let the logic deal with that later
            for (String c : colors) {
                // match color to drone
                Drone d = mDronesByColor.getOrDefault(c, null);
                if (d != null) {
                    long dBlT = mDroneBlackout.getOrDefault(c, 0L);
                    if ((dBlT + DBUtils.DETETECTION_BLACKOUT_TIME < timestamp)) {
                        // example: 10000 +
                        // we have one that is out of blackout time, add it.
                        drones.add(d);
                        // and add it back to blackout timed detection
                        mDroneBlackout.put(c, timestamp);
                    }
                }

            }
        } else {
            // we have only one, amazing
            Drone d = mDronesByColor.getOrDefault(color, null);
            if (d != null) {
                long dBlT = mDroneBlackout.getOrDefault(color, 0L);
                if ((dBlT + DBUtils.DETETECTION_BLACKOUT_TIME < timestamp)) {
                    drones.add(d);
                    // add to blackout times as well
                    mDroneBlackout.put(color, timestamp);
                }
            }
        }
        return drones;
    }

    public static void inputTimingEvent(DroneDetectedEvent dde) {
        mExecutor.execute(() -> {
            RecordedRecord rr = new RecordedRecord();
            rr.setRaceId(FPVDb.getDatabase(null).raceDao().currentRace().getUid());
            // calc delta
            RecordedRecord lastrecord = FPVDb.getDatabase(null)
                    .recordedRecordDao()
                    .getLastRecordForDroneAndRace(rr.getRaceId(), dde.getDetectedDrone().getTransponderid());

            rr.setDroneId(dde.getDetectedDrone().getTransponderid());
            if (lastrecord == null) {
                // first record detected
                rr.setFirst(true);
                rr.setDuration(0);
            } else {
                // we have completed a lap, calc the delta to the previous one
                rr.setDuration(dde.getTimestamp() - lastrecord.getTimestamp());
            }
            rr.setTimestamp(dde.getDetectedDrone().getTransponderid());
            rr.setTimestamp(dde.getTimestamp());
            mRecordedRecordDao.insertRecordedRecord(rr);
        });
    }

    public static void submitDroneToDB(Drone d) {
        mExecutor.execute(() -> {
            FPVDb fpvdb = FPVDb.getDatabase(null);
            DroneDao ddao = fpvdb.droneDao();
            Drone foundDrone = ddao.getDrone(d.getTransponderid());
            if (foundDrone != null) {
//                TODO: make this less possessive
//                TODO: read this from bluetooth device
//                foundDrone.setColor();
                foundDrone.setLastSeen(d.getLastSeen());
                ddao.updateDrone(foundDrone);
                initDroneMaps();
            } else {
                d.setColor("BLUE");
                fpvdb.droneDao().insert(d);
            }
        });
    }

    public static void submitDroneToDBFromDroneItem(DroneItemContent.DroneItem di) {
        if (HelperFunctionsCV.isInitialized) {
            mExecutor.execute(() -> {
                FPVDb fpvdb = FPVDb.getDatabase(null);
                long transponderid = Long.decode(di.droneId);
                Drone d = fpvdb.droneDao().getDrone(transponderid);
                if (d == null) {
                    d = new Drone();
                    d.setDronename(di.droneName);
                    d.setTransponderid(transponderid);
                    fpvdb.droneDao().insert(d);
                    initDroneMaps();
                }
            });
        }
    }

    public static void updateDroneFromDroneItem(DroneItemContent.DroneItem di) {
        if (HelperFunctionsCV.isInitialized) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    FPVDb fpvdb = FPVDb.getDatabase(null);
                    long transponderid = Long.decode(di.droneId);
                    Drone d = fpvdb.droneDao().getDrone(transponderid);
                    if (d != null) {
                        d.setDronename(di.droneName);
                        d.setTransponderid(transponderid);
                        fpvdb.droneDao().updateDrone(d);
                        initDroneMaps();
                    } else {
                        submitDroneToDBFromDroneItem(di);
                    }
                }
            });
        }
    }

    public static void updateDrone(Drone d) {
        if (HelperFunctionsCV.isInitialized) {
            mExecutor.execute(() -> {
                FPVDb fpvdb = FPVDb.getDatabase(null);
                fpvdb.droneDao().updateDrone(d);
                initDroneMaps();
            });
        }
    }

    public static void updateLastSeenByMac(String mac) {
        mExecutor.execute(() -> {
            FPVDb fpvdb = FPVDb.getDatabase(null);
            Drone d = fpvdb.droneDao().getDroneByMac(mac);
            d.setLastSeen(System.currentTimeMillis());
            fpvdb.droneDao().updateDrone(d);
        });
    }

    public static void insertRace(Race r) {
        mExecutor.execute(() -> {
            FPVDb fpvDb = FPVDb.getDatabase(null);
            fpvDb.raceDao().insertRace(r);
        });
    }

    public static void makeRaceActive(Race r) {
        mExecutor.execute(() -> {
            FPVDb fpvDb = FPVDb.getDatabase(null);
            fpvDb.runInTransaction(() -> {
                RaceDao raceDao = fpvDb.raceDao();
                raceDao.deactivateAllRaces();
                r.setActive(true);
                raceDao.updateRace(r);
            });
        });
    }

    public static void startCurrentRace() throws NoActiveRaceException {
        long now = System.currentTimeMillis();
        mExecutor.execute(() -> {
            // get current active race
            FPVDb fpvDb = FPVDb.getDatabase(null);
            Race currentRace = fpvDb.raceDao().currentRace();
            if (currentRace == null) {
                throw new NoActiveRaceException();
            } else {
                currentRace.setStarttimestamp(now);
                fpvDb.raceDao().updateRace(currentRace);
            }
        });
    }

    public static void updateRace(Race r) {
        mExecutor.execute(() -> {
            Objects.requireNonNull(FPVDb.getDatabase(null)).raceDao().updateRace(r);
        });
    }

    public static void getRaceAndDronesWithRecords(int raceid, ResultRaceDetailsAvailable cb) {
        mExecutor.execute(() -> {
            FPVDb fpvDb = FPVDb.getDatabase(null);
            Race currentRace = fpvDb.raceDao().getRace(raceid);
            List<DroneWithRecords> dwr = fpvDb.recordedRecordDao().getDronesWithRecordsForRace(raceid);
            ResultRaceDetails rrd = new ResultRaceDetails(currentRace, dwr);
            cb.onComplete(rrd);
        });
    }

    public static void clearDB(Context ctx) {
        mExecutor.execute(() -> {
            FPVDb fpvdb = FPVDb.getDatabase(ctx);
            fpvdb.clearAllTables();
            initDroneMaps();
        });
    }

}
