package fr.stein.maxbooker.domain.usecase

import android.util.Log
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.data.local.database.SncfReservationDao
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.data.mapper.toEntity
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.repository.sncf.SncfApiRepository
import javax.inject.Inject

class SncfApiFetchReservationsUseCase @Inject constructor(
    private val sncfApiRepository: SncfApiRepository,
    private val sncfReservationDao: SncfReservationDao
) {
    data class Output(val updatedSncfReservations: List<SncfReservation>, val newSncfReservations: List<SncfReservation>)

    @Throws(SncfApiException::class)
    suspend operator fun invoke(sncfCustomer: SncfCustomer): Output {
        Log.d("Max Book", "SncfApiFetchReservationsUseCase: fetching reservations...")

        val sncfReservations = runCatching {
            val sncfReservations = sncfApiRepository.getTravelConsultations(sncfCustomer)
            Log.d(
                "Max Book",
                "SncfApiFetchReservationsUseCase: received ${sncfReservations.size} reservations from API"
            )

            return@runCatching sncfReservations
        }.onFailure { throwable ->
            Log.e("Max Book", "SncfApiFetchReservationsUseCase: error fetching reservations", throwable)
        }.getOrThrow()

        return runCatching {
            val updatedSncfReservations = mutableListOf<SncfReservation>()
            val newSncfReservations = mutableListOf<SncfReservation>()

            for (reservation in sncfReservations) {
                val savedReservation = sncfReservationDao.getReservation(reservation.dvNumber, reservation.trainNumber)?.toDomain()

                if (savedReservation != null) {
                    reservation.updateDetails(
                        amount = savedReservation.amount,
                        exchangeable = savedReservation.exchangeable,
                        refundable = savedReservation.refundable,
                        seat = savedReservation.seat,
                        tcn = savedReservation.tcn,
                        transportationServiceOffer = savedReservation.transportationServiceOffer
                    )
                    updatedSncfReservations.add(reservation)
                } else {
                    sncfReservationDao.insertStationIfNotExists(reservation.origin.toEntity())
                    sncfReservationDao.insertStationIfNotExists(
                        reservation.destination.toEntity()
                    )
                    newSncfReservations.add(reservation)
                }
            }

            sncfReservationDao.upsertReservations(
                (updatedSncfReservations + newSncfReservations).map {
                    it.toEntity()
                }
            )

            return@runCatching Output(
                updatedSncfReservations = updatedSncfReservations,
                newSncfReservations = newSncfReservations
            )
        }.onFailure { throwable ->
            Log.e(
                "Max Book",
                "SncfApiFetchReservationsUseCase: error saving reservations in app database",
                throwable
            )
        }.getOrThrow()
    }
}
