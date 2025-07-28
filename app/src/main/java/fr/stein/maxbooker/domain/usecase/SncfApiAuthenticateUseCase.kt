package fr.stein.maxbooker.domain.usecase

import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.domain.model.sncf.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.sncf.SncfApiTokenRequest
import fr.stein.maxbooker.domain.repository.sncf.SncfApiAuthenticationRepository
import javax.inject.Inject

class SncfApiAuthenticateUseCase @Inject constructor(
    private val sncfApiAuthenticationRepository: SncfApiAuthenticationRepository
) {
    @Throws(SncfApiException::class)
    suspend fun authenticate(
        sncfApiTokenRequest: SncfApiTokenRequest,
        cookies: String
    ): SncfApiAuthentication {
        val sncfApiAuthentication = sncfApiAuthenticationRepository.authenticate(
            sncfApiTokenRequest,
            cookies
        )
        return sncfApiAuthentication
    }

    suspend fun getSavedAuthentication(): SncfApiAuthentication? =
        sncfApiAuthenticationRepository.getSncfApiAuthentication()

    suspend fun updateCookies(cookies: String): SncfApiAuthentication =
        sncfApiAuthenticationRepository.updateAuthenticationCookie(cookies)
}
