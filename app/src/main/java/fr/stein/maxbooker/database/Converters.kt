package fr.stein.maxbooker.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import fr.stein.maxbooker.database.reservations.SncfSeat
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {
    @TypeConverter
    fun sncfSeatToString(sncfSeat: SncfSeat): String {
        return Gson().toJson(sncfSeat)
    }

    @TypeConverter
    fun stringToSncfSeat(jsonString: String): SncfSeat {
        return Gson().fromJson(jsonString, SncfSeat::class.java)
    }

    @TypeConverter
    fun localDateTimeToLong(localDateTime: LocalDateTime): Long {
        return localDateTime.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun longToLocalDateTime(longTimestamp: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(longTimestamp, 0, ZoneOffset.UTC)
    }
}