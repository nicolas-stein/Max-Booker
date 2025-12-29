package fr.stein.maxbooker.domain.usecase

import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.repository.sncf.SncfApiRepository
import javax.inject.Inject

class SncfApiConfirmTravelUseCase @Inject constructor(private val sncfApiRepository: SncfApiRepository) {
    @Throws(SncfApiException::class)
    suspend operator fun invoke(sncfReservation: SncfReservation) {
        sncfApiRepository.confirmTravel(sncfReservation)
    }
}
