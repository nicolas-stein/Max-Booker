package fr.stein.maxbooker.database.reservations

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import java.time.LocalDateTime

@Dao
interface SncfReservationDao {
    @Query("SELECT * FROM SncfReservation WHERE order_id = :orderId AND origin_rrcode = :origin AND destination_rrcode = :destination")
    fun get(orderId: String, origin: String, destination: String): SncfReservation?

    @Query("SELECT * FROM SncfReservation")
    fun getAll(): List<SncfReservation>

    @Query("SELECT * FROM SncfReservation ORDER BY departure_datetime ASC")
    fun getAllObservable(): LiveData<List<SncfReservation>>

    @Query("SELECT * FROM SncfReservation WHERE departure_datetime > :afterDateTime")
    fun getAllLaterThanDateTime(afterDateTime: LocalDateTime): List<SncfReservation>

    @Update
    fun updateReservation(reservation: SncfReservation)

    @Upsert
    fun upsertReservations(reservations: List<SncfReservation>)

    @Query("DELETE FROM SncfReservation")
    fun deleteAll()
}