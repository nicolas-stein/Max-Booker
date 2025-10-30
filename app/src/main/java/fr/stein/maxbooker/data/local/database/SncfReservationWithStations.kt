package fr.stein.maxbooker.data.local.database

import androidx.room.Embedded
import androidx.room.Relation

data class SncfReservationWithStations(
    @Embedded val reservation: SncfReservationEntity,
    @Relation(
        parentColumn = "origin_rrcode",
        entityColumn = "rrCode"
    )
    val origin: SncfStationEntity,

    @Relation(
        parentColumn = "destination_rrcode",
        entityColumn = "rrCode"
    )
    val destination: SncfStationEntity
)
