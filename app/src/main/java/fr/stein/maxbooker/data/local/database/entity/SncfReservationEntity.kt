package fr.stein.maxbooker.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.ZonedDateTime

@Entity(primaryKeys = ["departure_datetime", "dv_number", "train_number"])
data class SncfReservationEntity(
    @ColumnInfo(name = "arrival_datetime") val arrivalDateTime: ZonedDateTime,
    @ColumnInfo(name = "avantage") val avantage: Boolean,
    @ColumnInfo(name = "coach_number") val coachNumber: String,
    @ColumnInfo(name = "departure_datetime") val departureDateTime: ZonedDateTime,
    @ColumnInfo(name = "destination_rrcode") val destination: String,
    @ColumnInfo(name = "dv_number") val dvNumber: String,
    @ColumnInfo(name = "order_id") val orderId: String,
    @ColumnInfo(name = "origin_rrcode") val origin: String,
    @ColumnInfo(name = "reservation_date") val reservationDate: ZonedDateTime,
    @ColumnInfo(name = "seat_number") val seatNumber: String,
    @ColumnInfo(name = "service_item_id") val serviceItemId: String,
    @ColumnInfo(name = "train_number") val trainNumber: String,
    @ColumnInfo(name = "travel_class") val travelClass: String,
    @ColumnInfo(name = "travel_confirmed") val travelConfirmed: String,
    @ColumnInfo(name = "travel_status") val travelStatus: String,
    @ColumnInfo(name = "customer_last_name") val customerLastName: String,

    @ColumnInfo(name = "amount") val amount: String?,
    @ColumnInfo(name = "exchangeable") val exchangeable: Boolean?,
    @ColumnInfo(name = "refundable") val refundable: Boolean?,
    @ColumnInfo(name = "seat") val seat: Seat?,
    @ColumnInfo(name = "tcn") val tcn: String?,
    @ColumnInfo(name = "transportation_service_offer") val transportationServiceOffer: String?
) {
    data class Seat(val facingForward: Boolean, val seatPosition: String, val spaceComfort: String?, val spaceType: String, val tgvDeck: String?)
}
