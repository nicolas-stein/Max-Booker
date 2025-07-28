package fr.stein.maxbooker.data.repository.sncf

import android.util.Log
import androidx.datastore.core.DataStore
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.data.mapper.toProto
import fr.stein.maxbooker.data.remote.sncf.SncfApi
import fr.stein.maxbooker.domain.model.sncf.SncfApiAuthentication
import fr.stein.maxbooker.domain.model.sncf.SncfApiTokenRequest
import fr.stein.maxbooker.domain.repository.sncf.SncfApiAuthenticationRepository
import fr.stein.maxbooker.domain.repository.sncf.SncfApiExecutor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SncfApiAuthenticationRepositoryImpl(
    private val sncfApi: SncfApi,
    private val sncfApiExecutor: SncfApiExecutor,
    private val sncfApiAuthenticationDataStore: DataStore<SncfApiAuthenticationProto>
) : SncfApiAuthenticationRepository {

    private val sncfApiAuthenticationFlow: Flow<SncfApiAuthentication?> =
        sncfApiAuthenticationDataStore.data.map { it.toDomain() }

    override suspend fun getSncfApiAuthentication(): SncfApiAuthentication? =
        sncfApiAuthenticationFlow.first()

    override suspend fun authenticate(
        sncfApiTokenRequest: SncfApiTokenRequest,
        cookies: String
    ): SncfApiAuthentication {
        Log.d("Max Book", "SncfApiRepositoryImpl: requested authenticate")
        val tokenDto = sncfApiExecutor.execute {
            sncfApi.getSncfApiToken(sncfApiTokenRequest, cookies)
        }
        val newSncfApiAuthentication = SncfApiAuthentication(tokenDto.toDomain(), cookies)
        sncfApiAuthenticationDataStore.updateData { newSncfApiAuthentication.toProto() }

        return newSncfApiAuthentication
    }

    @Throws(SncfApiException.AuthenticatedRequired::class)
    override suspend fun getAuthorizationHeader(): String? {
        val idToken = getSncfApiAuthentication()?.sncfApiToken?.idToken
        return if (idToken == null) {
            null
        } else {
            "Bearer $idToken"
        }
    }

    override suspend fun getCookies(): String? = getSncfApiAuthentication()?.cookies

    override suspend fun refreshAuthentication(): SncfApiAuthentication {
        val sncfApiAuthentication = getSncfApiAuthentication()
        if (sncfApiAuthentication == null) {
            throw SncfApiException.AuthenticatedRequired()
        }

        return authenticate(
            SncfApiTokenRequest(
                refreshToken = sncfApiAuthentication.sncfApiToken.refreshToken,
                redirectUri = "https://maxjeune-tgvinoui.sncf/auth/login/redirect",
                type = "REFRESH_TOKEN"
            ),
            sncfApiAuthentication.cookies
        )
    }

    override suspend fun updateAuthenticationCookie(cookies: String): SncfApiAuthentication {
        val currentSncfApiAuthentication = getSncfApiAuthentication()
        if (currentSncfApiAuthentication == null) {
            throw SncfApiException.AuthenticatedRequired()
        }

        val newSncfApiAuthentication = SncfApiAuthentication(
            sncfApiToken = currentSncfApiAuthentication.sncfApiToken,
            cookies = cookies
        )

        sncfApiAuthenticationDataStore.updateData { newSncfApiAuthentication.toProto() }
        return newSncfApiAuthentication
    }
}
