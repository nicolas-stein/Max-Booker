package fr.stein.maxbooker.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SncfReservationDao {
    @Query("SELECT * FROM SncfReservationEntity")
    @Transaction
    suspend fun getAllReservations(): List<SncfReservationWithStations>

    @Query("SELECT * FROM SncfReservationEntity")
    @Transaction
    fun observeAllReservations(): Flow<List<SncfReservationWithStations>>

    @Query("SELECT * FROM SncfReservationEntity WHERE order_id IN (:orderIds)")
    @Transaction
    suspend fun getReservationsByIds(orderIds: List<String>): List<SncfReservationWithStations>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStationIfNotExists(sncfStationEntity: SncfStationEntity)

    @Upsert
    suspend fun upsertReservation(sncfReservation: SncfReservationEntity)

    @Upsert
    suspend fun upsertReservations(sncfReservationEntities: List<SncfReservationEntity>)
}
