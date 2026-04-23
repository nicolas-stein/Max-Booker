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
    data class Output(val sncfReservations: List<SncfReservation>, val newSncfReservationsDvNumber: List<String>)

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
            val dvNumbers = sncfReservations.map { it.dvNumber }
            val savedSncfReservations = sncfReservationDao.getReservationsByIds(dvNumbers)
                .map { it.toDomain() }
                .associateBy { it.dvNumber }

            val sncfReservationsToUpsert = mutableListOf<SncfReservation>()
            val newSncfReservationsDvNumber = mutableListOf<String>()

            for (reservation in sncfReservations) {
                val savedReservation = savedSncfReservations[reservation.dvNumber]

                if (savedReservation != null) {
                    reservation.updateDetails(
                        amount = savedReservation.amount,
                        exchangeable = savedReservation.exchangeable,
                        refundable = savedReservation.refundable,
                        seat = savedReservation.seat,
                        tcn = savedReservation.tcn,
                        transportationServiceOffer = savedReservation.transportationServiceOffer
                    )
                } else {
                    sncfReservationDao.insertStationIfNotExists(reservation.origin.toEntity())
                    sncfReservationDao.insertStationIfNotExists(
                        reservation.destination.toEntity()
                    )
                    newSncfReservationsDvNumber.add(reservation.dvNumber)
                }

                sncfReservationsToUpsert.add(reservation)
            }

            sncfReservationDao.upsertReservations(
                sncfReservationsToUpsert.map {
                    it.toEntity()
                }
            )

            return@runCatching Output(
                sncfReservations = sncfReservationsToUpsert,
                newSncfReservationsDvNumber = newSncfReservationsDvNumber
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
