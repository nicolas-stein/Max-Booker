package fr.stein.maxbooker.domain.usecase

import fr.stein.maxbooker.data.exception.SncfApiRepositoryException
import fr.stein.maxbooker.domain.model.sncf.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.sncf.SncfApiTokenRequest
import fr.stein.maxbooker.domain.repository.SncfApiRepository
import javax.inject.Inject

class SncfApiAuthenticateUseCase @Inject constructor(
    private val sncfApiRepository: SncfApiRepository
) {
    @Throws(SncfApiRepositoryException::class)
    suspend operator fun invoke(
        sncfApiTokenRequest: SncfApiTokenRequest,
        cookies: String
    ): SncfApiAuthentication {
        val sncfApiAuthentication = sncfApiRepository.authenticate(sncfApiTokenRequest, cookies)
        return sncfApiAuthentication
    }
}
