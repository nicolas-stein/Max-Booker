package fr.stein.maxbooker.domain.usecase

import fr.stein.maxbooker.data.exception.SncfRepositoryException
import fr.stein.maxbooker.domain.model.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.SncfApiTokenRequest
import fr.stein.maxbooker.domain.repository.SncfApiRepository
import javax.inject.Inject

class SncfApiAuthenticateUseCase @Inject constructor(
    private val sncfApiRepository: SncfApiRepository
) {
    @Throws(SncfRepositoryException::class)
    suspend operator fun invoke(sncfApiTokenRequest: SncfApiTokenRequest, cookies: String): SncfApiAuthentication {
        val sncfApiAuthentication = sncfApiRepository.authenticate(sncfApiTokenRequest, cookies)
        sncfApiRepository.storeSncfApiAuthentication(sncfApiAuthentication)

        return sncfApiAuthentication
    }
}