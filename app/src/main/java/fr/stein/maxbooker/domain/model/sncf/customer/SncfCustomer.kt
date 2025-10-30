package fr.stein.maxbooker.domain.model.sncf.customer

import java.time.LocalDate
import java.time.ZonedDateTime

data class SncfCustomer(
    val iuc: String,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
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
    val pictureUpdate: ZonedDateTime,
    val cniUpdate: ZonedDateTime,
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
