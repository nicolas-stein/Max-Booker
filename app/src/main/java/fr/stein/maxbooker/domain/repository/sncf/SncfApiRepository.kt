package fr.stein.maxbooker.domain.repository.sncf

import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation

interface SncfApiRepository {

    @Throws(SncfApiException::class)
    suspend fun getCustomer(cookiesOverride: String?): SncfCustomer

    @Throws(SncfApiException::class)
    suspend fun getTravelConsultations(sncfCustomer: SncfCustomer): List<SncfReservation>

    @Throws(SncfApiException::class)
    suspend fun getTravel(sncfCustomer: SncfCustomer, sncfReservation: SncfReservation): SncfReservation

    @Throws(SncfApiException::class)
    suspend fun confirmTravel(sncfReservation: SncfReservation)
}
