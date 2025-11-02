package fr.stein.maxbooker.domain.model.sncf.reservation

data class SncfGetTravelRequest(
    val customerName: String,
    val departureDateTime: String,
    val marketingCarrierRef: String,
    val trainNumber: String
)
