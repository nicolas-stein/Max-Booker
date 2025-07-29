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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SncfApiAuthenticationRepositoryImpl(
    private val sncfApi: SncfApi,
    private val sncfApiExecutor: SncfApiExecutor,
    private val sncfApiAuthenticationDataStore: DataStore<SncfApiAuthenticationProto>
) : SncfApiAuthenticationRepository {

    private val _sncfApiAuthenticationFlow = MutableStateFlow<SncfApiAuthentication?>(null)
    val sncfApiAuthenticationFlow: StateFlow<SncfApiAuthentication?> = _sncfApiAuthenticationFlow

    init {
        runBlocking {
            _sncfApiAuthenticationFlow.value =
                sncfApiAuthenticationDataStore.data.first().toDomain()
        }

        CoroutineScope(Dispatchers.IO).launch {
            sncfApiAuthenticationDataStore.data.collect { sncfApiAuthentication ->
                _sncfApiAuthenticationFlow.value = sncfApiAuthentication.toDomain()
            }
        }
    }

    override suspend fun getSncfApiAuthentication(): SncfApiAuthentication? =
        sncfApiAuthenticationFlow.value

    override suspend fun authenticate(
        sncfApiTokenRequest: SncfApiTokenRequest,
        cookies: String
    ): SncfApiAuthentication {
        Log.d("Max Book", "SncfApiAuthenticationRepositoryImpl: requested authenticate")
        val tokenDto = sncfApiExecutor.execute {
            sncfApi.getSncfApiToken(sncfApiTokenRequest, cookies)
        }
        val newSncfApiAuthentication = SncfApiAuthentication(tokenDto.toDomain(), cookies)
        sncfApiAuthenticationDataStore.updateData { newSncfApiAuthentication.toProto() }
        Log.d(
            "Max Book",
            "SncfApiAuthenticationRepositoryImpl: saved new authentication credentials"
        )

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
