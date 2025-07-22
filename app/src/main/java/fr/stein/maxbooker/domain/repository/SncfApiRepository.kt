package fr.stein.maxbooker.domain.repository

import fr.stein.maxbooker.domain.model.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.SncfApiTokenRequest
import kotlinx.coroutines.flow.Flow

interface SncfApiRepository {
    val sncfApiAuthentication: Flow<SncfApiAuthentication>
    suspend fun retrieveSncfApiToken(sncfApiTokenRequest: SncfApiTokenRequest, cookies: String): SncfApiAuthentication
    suspend fun refreshSncfApiToken()
}