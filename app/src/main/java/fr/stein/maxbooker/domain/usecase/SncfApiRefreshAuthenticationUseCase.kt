package fr.stein.maxbooker.domain.usecase

import fr.stein.maxbooker.data.exception.SncfRepositoryException
import fr.stein.maxbooker.domain.model.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.SncfApiTokenRequest
import fr.stein.maxbooker.domain.repository.SncfApiRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class SncfApiRefreshAuthenticationUseCase @Inject constructor(
    private val sncfApiRepository: SncfApiRepository,
    private val sncfApiAuthenticateUseCase: SncfApiAuthenticateUseCase
) {
    @Throws(SncfRepositoryException::class)
    suspend operator fun invoke(
        sncfApiTokenRequest: SncfApiTokenRequest,
        cookies: String
    ): SncfApiAuthentication {
        val currentSncfApiAuthentication = sncfApiRepository.sncfApiAuthentication.first()

        val sncfApiAuthentication = sncfApiAuthenticateUseCase.invoke(
            SncfApiTokenRequest(
                authCode = null,
                refreshToken = currentSncfApiAuthentication.sncfApiToken.refreshToken,
                redirectUri = "https://maxjeune-tgvinoui.sncf/auth/login/redirect",
                type = "REFRESH_TOKEN"
            ),
            currentSncfApiAuthentication.cookies
        )
        sncfApiRepository.storeSncfApiAuthentication(sncfApiAuthentication)

        return sncfApiAuthentication
    }
}
