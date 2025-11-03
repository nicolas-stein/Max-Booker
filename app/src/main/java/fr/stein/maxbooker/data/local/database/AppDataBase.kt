package fr.stein.maxbooker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.stein.maxbooker.data.local.database.entity.SncfReservationEntity
import fr.stein.maxbooker.data.local.database.entity.SncfStationEntity

@Database(entities = [SncfReservationEntity::class, SncfStationEntity::class], version = 1)
@TypeConverters(AppDataBaseConverters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun sncfReservationDao(): SncfReservationDao
}
