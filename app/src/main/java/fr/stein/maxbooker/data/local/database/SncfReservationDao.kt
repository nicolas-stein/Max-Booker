package fr.stein.maxbooker.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import fr.stein.maxbooker.data.local.database.entity.SncfReservationEntity
import fr.stein.maxbooker.data.local.database.entity.SncfStationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SncfReservationDao {
    @Query("SELECT * FROM SncfReservationEntity")
    @Transaction
    suspend fun getAllReservations(): List<SncfReservationWithStations>

    @Query("SELECT * FROM SncfReservationEntity")
    @Transaction
    fun observeAllReservations(): Flow<List<SncfReservationWithStations>>

    @Query("SELECT * FROM SncfReservationEntity WHERE dv_number=:dvNumber")
    @Transaction
    suspend fun getReservationById(dvNumber: String): SncfReservationWithStations?

    @Query("SELECT * FROM SncfReservationEntity WHERE dv_number IN (:dvNumbers)")
    @Transaction
    suspend fun getReservationsByIds(dvNumbers: List<String>): List<SncfReservationWithStations>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStationIfNotExists(sncfStationEntity: SncfStationEntity)

    @Upsert
    suspend fun upsertReservation(sncfReservation: SncfReservationEntity)

    @Upsert
    suspend fun upsertReservations(sncfReservationEntities: List<SncfReservationEntity>)

    @Delete
    suspend fun deleteReservation(sncfReservation: SncfReservationEntity)
}
