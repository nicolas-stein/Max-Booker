package fr.stein.maxbooker.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.data.mapper.toProto
import fr.stein.maxbooker.data.remote.SncfApi
import fr.stein.maxbooker.domain.model.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.SncfApiTokenRequest
import fr.stein.maxbooker.domain.repository.SncfApiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SncfApiRepositoryImpl(
    private val sncfApi: SncfApi,
    private val sncfApiAuthenticationDataStore: DataStore<SncfApiAuthenticationProto>
): SncfApiRepository {

    override val sncfApiAuthentication: Flow<SncfApiAuthentication> = sncfApiAuthenticationDataStore.data.map {
        SncfApiAuthentication(
            it.sncfApiToken.toDomain(),
            it.cookies
        )
    }

    override suspend fun retrieveSncfApiToken(sncfApiTokenRequest: SncfApiTokenRequest, cookies: String): SncfApiAuthentication {
        val sncfApiToken = sncfApi.getSncfApiToken(sncfApiTokenRequest, cookies)
        Log.d("Max Book", "retrieveSncfApiToken: got sncf api token !! $sncfApiToken")

        val sncfApiAuthentication = SncfApiAuthentication(
            sncfApiToken.toDomain(),
            cookies
        )
        sncfApiAuthenticationDataStore.updateData { sncfApiAuthentication.toProto() }

        return sncfApiAuthentication
    }

    override suspend fun refreshSncfApiToken() {
        TODO("Not yet implemented")
    }
}