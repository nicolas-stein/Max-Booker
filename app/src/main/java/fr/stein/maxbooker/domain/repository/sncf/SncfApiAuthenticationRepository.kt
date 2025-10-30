package fr.stein.maxbooker.domain.repository.sncf

import fr.stein.maxbooker.domain.model.sncf.auth.SncfApiAuthentication

interface SncfApiAuthenticationRepository {
    suspend fun getSncfApiAuthentication(): SncfApiAuthentication?
    suspend fun updateAuthenticationCookie(cookies: String): SncfApiAuthentication?
    suspend fun refreshAuthenticationCookie(cookies: String): SncfApiAuthentication?
}
