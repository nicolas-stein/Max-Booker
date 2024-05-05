package fr.stein.maxbooker.api.data

import fr.stein.maxbooker.database.reservations.SncfReservation
import java.time.LocalDateTime

data class SncfApiReservation(
    val orderId: String,
    val dvNumber: String,
    val origin: SncfApiStation,
    val destination: SncfApiStation,
    val departureDateTime: String,
    val arrivalDateTime: String,
    val travelClass: String,
    val trainNumber: String,
    val coachNumber: String,
    val seatNumber: String,
    val reservationDate: String,
    val travelConfirmed: String,
    val travelStatus: String
) {

    fun toSncfReservation(): SncfReservation {
        return SncfReservation(
            orderId = orderId,
            dvNumber = dvNumber,
            origin = origin.rrCode,
            destination = destination.rrCode,
            departureDateTime = LocalDateTime.parse(departureDateTime),
            arrivalDateTime = LocalDateTime.parse(arrivalDateTime),
            travelClass = travelClass,
            trainNumber = trainNumber,
            coachNumber = coachNumber,
            seatNumber = seatNumber,
            reservationDate = reservationDate,
            travelConfirmed = travelConfirmed,
            travelStatus = travelStatus
        )
    }
}
