package fr.stein.maxbooker.domain.usecase

import android.util.Log
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.data.local.database.SncfReservationDao
import fr.stein.maxbooker.data.mapper.toEntity
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.repository.sncf.SncfApiRepository
import javax.inject.Inject

class SncfApiFetchReservationDetailUseCase @Inject constructor(
    private val sncfApiRepository: SncfApiRepository,
    private val sncfReservationDao: SncfReservationDao
) {
    @Throws(SncfApiException::class)
    suspend operator fun invoke(customer: SncfCustomer, sncfReservation: SncfReservation): SncfReservation {
        return runCatching {
            Log.d("Max Book", "SncfApiFetchReservationDetailUseCase: fetching reservation ${sncfReservation.dvNumber}-${sncfReservation.trainNumber}")
            val sncfReservationDetailed = sncfApiRepository.getTravel(customer, sncfReservation)
            return@runCatching sncfReservationDetailed
        }.onSuccess { sncfReservationDetailed ->
            Log.d(
                "Max Book",
                "SncfApiFetchReservationDetailUseCase: successfully fetched reservation ${sncfReservation.dvNumber}-${sncfReservation.trainNumber}"
            )
            return runCatching {
                sncfReservationDao.upsertReservation(sncfReservationDetailed.toEntity())
                return@runCatching sncfReservationDetailed
            }.onFailure { throwable ->
                Log.e(
                    "Max Book",
                    "SncfApiFetchReservationDetailUseCase: failed to save sncf reservation details to database",
                    throwable
                )
            }.getOrThrow()
        }.onFailure { throwable ->
            Log.e(
                "Max Book",
                "SncfApiFetchReservationDetailUseCase: failed to fetch sncf reservation details from API",
                throwable
            )
        }.getOrThrow()
    }
}
