package fr.stein.maxbooker.api.data

import fr.stein.maxbooker.database.reservations.SncfSeat

data class SncfApiTravel(
    val orderId: String,
    val serviceItemId: String,
    val dvNumber: String,
    val origin: SncfApiStation,
    val destination: SncfApiStation,
    private val departureDateTime: String,
    private val arrivalDateTime: String,
    val travelClass: String,
    val trainNumber: String,
    val transportationServiceOffer: String,
    val tcn: String,
    val coachNumber: String,
    val seatNumber: String,
    val seat: SncfApiTravelSeat,
    val refundable: Boolean,
    val exchangeable: Boolean
)

data class SncfApiTravelSeat (
    val facingForward: Boolean,
    val seatPosition: String,
    val spaceType: String?,
    val tgvDeck: String?
) {

    fun toSncfSeat(): SncfSeat {
        return SncfSeat(facingForward = facingForward,
            seatPosition = seatPosition,
            spaceType = spaceType,
            tgvDeck = tgvDeck)
    }
}