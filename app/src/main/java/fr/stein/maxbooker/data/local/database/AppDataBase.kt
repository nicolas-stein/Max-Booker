package fr.stein.maxbooker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SncfReservationEntity::class, SncfStationEntity::class], version = 1)
@TypeConverters(AppDataBaseConverters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun sncfReservationDao(): SncfReservationDao
}
