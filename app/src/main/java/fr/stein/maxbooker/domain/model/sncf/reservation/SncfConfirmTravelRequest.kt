package fr.stein.maxbooker.domain.model.sncf.reservation

data class SncfConfirmTravelRequest(
    val marketingCarrierRef: String,
    val trainNumber: String,
    val departureDateTime: String
)
