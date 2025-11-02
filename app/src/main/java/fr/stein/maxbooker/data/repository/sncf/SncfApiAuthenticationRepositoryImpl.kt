package fr.stein.maxbooker.data.repository.sncf

import android.util.Log
import androidx.datastore.core.DataStore
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.data.mapper.toProto
import fr.stein.maxbooker.data.remote.sncf.SncfApi
import fr.stein.maxbooker.domain.model.sncf.auth.SncfApiAuthentication
import fr.stein.maxbooker.domain.repository.sncf.SncfApiAuthenticationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SncfApiAuthenticationRepositoryImpl(
    private val sncfApi: SncfApi,
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

    override suspend fun updateAuthenticationCookie(cookies: String): SncfApiAuthentication {
        val newSncfApiAuthentication = SncfApiAuthentication(cookies = cookies)

        sncfApiAuthenticationDataStore.updateData { newSncfApiAuthentication.toProto() }
        return newSncfApiAuthentication
    }

    override suspend fun refreshAuthenticationCookie(cookies: String): SncfApiAuthentication? {
        Log.d(
            "Max Book",
            "SncfApiAuthenticationRepositoryImpl: refreshAuthenticationCookie triggered"
        )
        try {
            val refreshAuthResponse = sncfApi.refreshAuth(cookies)

            if (refreshAuthResponse.isSuccessful) {
                val newCookies = refreshAuthResponse.headers().values("Set-Cookie")
                    .joinToString("; ") { it.substringBefore(";").trim() }
                if (newCookies.isBlank()) {
                    throw RuntimeException(
                        "Refresh authentication response does not contain Set-Cookie header !"
                    )
                }

                val newSncfApiAuthentication = SncfApiAuthentication(cookies = newCookies)
                sncfApiAuthenticationDataStore.updateData { newSncfApiAuthentication.toProto() }
                return newSncfApiAuthentication
            } else {
                val errorBody = refreshAuthResponse.errorBody()?.string()
                throw SncfApiException.ApiErrorException(
                    refreshAuthResponse.code(),
                    errorBody
                )
            }
        } catch (e: Exception) {
            Log.e("Max Book", "Unable to refresh authentication", e)
        }

        return null
    }
}
