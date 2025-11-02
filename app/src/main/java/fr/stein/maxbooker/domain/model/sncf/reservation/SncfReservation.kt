package fr.stein.maxbooker.domain.model.sncf.reservation

import java.time.ZonedDateTime

data class SncfReservation(
    val arrivalDateTime: ZonedDateTime,
    val avantage: Boolean,
    val coachNumber: String,
    val departureDateTime: ZonedDateTime,
    val destination: SncfStation,
    val dvNumber: String,
    val orderId: String,
    val origin: SncfStation,
    val reservationDate: ZonedDateTime,
    val seatNumber: String,
    val serviceItemId: String,
    val trainNumber: String,
    val travelClass: String,
    val travelConfirmed: String,
    val travelStatus: String,

    var amount: String? = null,
    var exchangeable: Boolean? = null,
    var refundable: Boolean? = null,
    var seat: Seat? = null,
    var tcn: String? = null,
    var transportationServiceOffer: String? = null
) {
    data class Seat(
        val facingForward: Boolean,
        val seatPosition: String,
        val spaceType: String,
        val tgvDeck: String
    )

    fun updateDetails(
        amount: String?,
        exchangeable: Boolean?,
        refundable: Boolean?,
        seat: Seat?,
        tcn: String?,
        transportationServiceOffer: String?
    ): SncfReservation {
        this.amount = amount
        this.exchangeable = exchangeable
        this.refundable = refundable
        this.seat = seat
        this.tcn = tcn
        this.transportationServiceOffer = transportationServiceOffer

        return this
    }
}
