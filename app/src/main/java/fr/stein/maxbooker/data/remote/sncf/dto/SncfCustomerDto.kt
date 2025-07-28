package fr.stein.maxbooker.data.remote.sncf.dto

data class SncfCustomerDto(
    val iuc: String,
    val createdAt: String,
    val updatedAt: String,
    val civility: String,
    val lastName: String,
    val firstName: String,
    val birthDate: String,
    val language: String,
    val address: String,
    val zipCode: String,
    val city: String,
    val country: String,
    val email: String,
    val mobilePhone: String,
    val nsdStatus: String,
    val pictureCounter: Int,
    val pictureStatus: String,
    val maxTravelsPerDay: Int,
    val pictureUpdate: String,
    val cniUpdate: String,
    val cniType: String,
    val cniValue: String,
    val cards: List<SncfCustomerCardDto>
)

data class SncfCustomerCardDto(
    val cardNumber: String,
    val marketingCarrierRef: String,
    val productType: String,
    val contractStatus: String,
    val validityStartDate: String,
    val validityEndDate: String,
    val ticketlessIndicator: Boolean
)
