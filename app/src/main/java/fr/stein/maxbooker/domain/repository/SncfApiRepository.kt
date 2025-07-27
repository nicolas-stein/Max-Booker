package fr.stein.maxbooker.domain.repository

import fr.stein.maxbooker.domain.model.sncf.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.sncf.SncfApiTokenRequest
import fr.stein.maxbooker.domain.model.sncf.SncfCustomer

interface SncfApiRepository {
    suspend fun authenticate(
        sncfApiTokenRequest: SncfApiTokenRequest,
        cookies: String
    ): SncfApiAuthentication

    suspend fun getCustomer(): SncfCustomer
}
