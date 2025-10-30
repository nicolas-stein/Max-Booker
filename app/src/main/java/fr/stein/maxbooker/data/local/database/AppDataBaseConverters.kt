package fr.stein.maxbooker.data.local.database

import androidx.room.TypeConverter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.time.ZonedDateTime

class AppDataBaseConverters {
    @TypeConverter
    fun fromZonedDateTime(value: ZonedDateTime?): String? = value?.toString()

    @TypeConverter
    fun toZonedDateTime(value: String?): ZonedDateTime? = value?.let { ZonedDateTime.parse(it) }

    @TypeConverter
    fun fromSncfReservationSeat(value: SncfReservationEntity.Seat?): String? =
        if (value != null) jacksonObjectMapper().writeValueAsString(value) else null

    @TypeConverter
    fun toSncfReservationSeat(value: String?): SncfReservationEntity.Seat? = if (value !=
        null
    ) {
        jacksonObjectMapper().readValue(value) as SncfReservationEntity.Seat
    } else {
        null
    }
}
