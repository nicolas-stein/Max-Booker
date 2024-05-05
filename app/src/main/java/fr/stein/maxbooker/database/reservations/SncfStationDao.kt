package fr.stein.maxbooker.database.reservations

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SncfStationDao {
    @Query("SELECT * FROM SncfStation WHERE rrcode = :rrcode")
    fun get(rrcode: String): SncfStation

    @Query("SELECT * FROM SncfStation")
    fun getAllObservable(): LiveData<List<SncfStation>>

    @Upsert
    fun upsertStations(stations: List<SncfStation>)
}