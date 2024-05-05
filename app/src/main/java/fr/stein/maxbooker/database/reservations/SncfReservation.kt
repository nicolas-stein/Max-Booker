package fr.stein.maxbooker.database.reservations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.stein.maxbooker.api.data.SncfApiTravel
import java.time.LocalDateTime

@Entity(primaryKeys = ["order_id", "origin_rrcode", "destination_rrcode"])
data class SncfReservation(
    @ColumnInfo(name = "order_id") val orderId: String,
    @ColumnInfo(name = "dv_number") val dvNumber: String,
    @ColumnInfo(name = "origin_rrcode") val origin: String,
    @ColumnInfo(name = "destination_rrcode") val destination: String,
    @ColumnInfo(name = "departure_datetime") val departureDateTime: LocalDateTime,
    @ColumnInfo(name = "arrival_datetime") val arrivalDateTime: LocalDateTime,
    @ColumnInfo(name = "travel_class") val travelClass: String,
    @ColumnInfo(name = "train_number") val trainNumber: String,
    @ColumnInfo(name = "coach_number") val coachNumber: String,
    @ColumnInfo(name = "seat_number") val seatNumber: String,
    @ColumnInfo(name = "reservation_date") val reservationDate: String,
    @ColumnInfo(name = "travel_confirmed") val travelConfirmed: String,
    @ColumnInfo(name = "travel_status") val travelStatus: String,
    @ColumnInfo(name = "service_item_id") var serviceItemId: String? = null,
    @ColumnInfo(name = "transportation_service_offer") var transportationServiceOffer: String? = null,
    @ColumnInfo(name = "tcn") var tcn: String? = null,
    @ColumnInfo(name = "seat") var seat: SncfSeat? = null,
    @ColumnInfo(name = "refundable") var refundable: Boolean? = null,
    @ColumnInfo(name = "exchangeable") var exchangeable: Boolean? = null

    ) {

    fun setTravelDetails(sncfApiTravel: SncfApiTravel) {
        serviceItemId = sncfApiTravel.serviceItemId
        transportationServiceOffer = sncfApiTravel.transportationServiceOffer
        tcn = sncfApiTravel.tcn
        seat = sncfApiTravel.seat.toSncfSeat()
        refundable = sncfApiTravel.refundable
        exchangeable = sncfApiTravel.exchangeable
    }
}