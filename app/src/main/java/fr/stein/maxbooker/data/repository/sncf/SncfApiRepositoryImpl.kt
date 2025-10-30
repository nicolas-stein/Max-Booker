package fr.stein.maxbooker.data.repository.sncf

import android.util.Log
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.data.remote.sncf.SncfApi
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomerRequest
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfTravelConsultationRequest
import fr.stein.maxbooker.domain.repository.sncf.SncfApiExecutor
import fr.stein.maxbooker.domain.repository.sncf.SncfApiRepository
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class SncfApiRepositoryImpl(
    private val sncfApi: SncfApi,
    private val sncfApiExecutor: SncfApiExecutor
) : SncfApiRepository {

    @Throws(SncfApiException::class)
    override suspend fun getCustomer(cookiesOverride: String?): SncfCustomer {
        Log.d("Max Book", "SncfApiRepositoryImpl: requested getCustomer")
        val sncfCustomerDto = sncfApiExecutor.execute {
            sncfApi.getCustomer(
                SncfCustomerRequest(
                    productTypes = listOf("TGV_MAX_JEUNE", "FIDEL", "IDTGV_MAX")
                ),
                cookiesOverride = cookiesOverride
            )
        }
        val sncfCustomer = sncfCustomerDto.toDomain()
        return sncfCustomer
    }

    @Throws(SncfApiException::class)
    override suspend fun getTravelConsultations(sncfCustomer: SncfCustomer): List<SncfReservation> {
        Log.d("Max Book", "SncfApiRepositoryImpl: requested getTravelConsultations")
        val sncfTravelConsultationDto = sncfApiExecutor.execute {
            sncfApi.getTravelConsultation(
                SncfTravelConsultationRequest(
                    cardNumber = sncfCustomer.cards[0].cardNumber,
                    startDate = ZonedDateTime.now(ZoneOffset.UTC)
                        .minusMonths(1)
                        .format(DateTimeFormatter.ISO_INSTANT)
                )
            )
        }
        val sncfReservations = sncfTravelConsultationDto.map { it.toDomain() }
        return sncfReservations
    }
}
