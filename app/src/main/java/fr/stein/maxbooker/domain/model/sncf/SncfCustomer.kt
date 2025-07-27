package fr.stein.maxbooker.domain.model.sncf

import java.time.LocalDate
import java.time.LocalDateTime

data class SncfCustomer(
    val iuc: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val civility: String,
    val lastName: String,
    val firstName: String,
    val birthDate: LocalDate,
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
    val pictureUpdate: LocalDateTime,
    val cniUpdate: LocalDateTime,
    val cniType: String,
    val cniValue: String,
    val cards: List<SncfCustomerCard>
)

data class SncfCustomerCard(
    val cardNumber: String,
    val marketingCarrierRef: String,
    val productType: String,
    val contractStatus: String,
    val validityStartDate: LocalDate,
    val validityEndDate: LocalDate,
    val ticketlessIndicator: Boolean
)
