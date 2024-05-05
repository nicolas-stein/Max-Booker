package fr.stein.maxbooker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.stein.maxbooker.database.reservations.SncfReservation
import fr.stein.maxbooker.database.reservations.SncfReservationDao
import fr.stein.maxbooker.database.reservations.SncfStation
import fr.stein.maxbooker.database.reservations.SncfStationDao

@Database(entities = [SncfReservation::class, SncfStation::class],version = 1)
@TypeConverters(Converters::class)
abstract class MaxBookerDatabase : RoomDatabase(){
    abstract fun sncfReservationDao(): SncfReservationDao
    abstract fun sncfStationDao(): SncfStationDao

    companion object {
        private const val DATABASE_NAME = "maxbook-db"

        @Volatile
        private var INSTANCE: MaxBookerDatabase? = null

        fun getInstance(context: Context): MaxBookerDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, MaxBookerDatabase::class.java, DATABASE_NAME).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

