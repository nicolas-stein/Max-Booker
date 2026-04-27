package fr.stein.maxbooker.data.remote.sncf.dto

data class SncfGetTravelDto(
    val amount: String,
    val arrivalDateTime: String,
    val coachNumber: String,
    val departureDateTime: String,
    val destination: SncfStationDto,
    val dvNumber: String,
    val exchangeable: Boolean,
    val orderId: String,
    val origin: SncfStationDto,
    val refundable: Boolean,
    val seat: Seat,
    val seatNumber: String,
    val serviceItemId: String,
    val tcn: String,
    val trainNumber: String,
    val transportationServiceOffer: String,
    val travelClass: String
) {
    data class Seat(val facingForward: Boolean, val seatPosition: String, val spaceComfort: String?, val spaceType: String, val tgvDeck: String?)
}
