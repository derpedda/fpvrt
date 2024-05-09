package net.pedda.fpvracetimer.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface RaceDao {

    @Query("SELECT * FROM races;")
    fun getAllRacesObservable(): LiveData<List<Race>>

    @Query("SELECT * FROM races;")
    fun getAllRaces(): List<Race>

    @Insert
    fun insertRace(r: Race)

    @Update
    fun updateRace(r: Race)

    @Query("UPDATE races SET isActive=0;")
    fun deactivateAllRaces()

    @Query("SELECT * FROM races WHERE isActive=1 LIMIT 1")
    fun currentRace(): Race

    @Query("SELECT * FROM races WHERE isActive=1 LIMIT 1")
    fun currentRaceObservable(): LiveData<Race>

    @Transaction
    @Query("SELECT * FROM races")
    fun getRacesWithDrones(): List<RaceWithDrones>


    @Query("SELECT * FROM races WHERE raceid = :raceid LIMIT 1")
    fun getRace(raceid: Long): Race


}