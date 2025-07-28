package fr.stein.maxbooker.domain.repository.sncf

import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.domain.model.sncf.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.sncf.SncfApiTokenRequest

interface SncfApiAuthenticationRepository {

    @Throws(SncfApiException::class)
    suspend fun authenticate(
        sncfApiTokenRequest: SncfApiTokenRequest,
        cookies: String
    ): SncfApiAuthentication

    suspend fun getSncfApiAuthentication(): SncfApiAuthentication?
    suspend fun getAuthorizationHeader(): String?
    suspend fun getCookies(): String?

    @Throws(SncfApiException::class)
    suspend fun refreshAuthentication(): SncfApiAuthentication?

    @Throws(SncfApiException.AuthenticatedRequired::class)
    suspend fun updateAuthenticationCookie(cookies: String): SncfApiAuthentication
}
