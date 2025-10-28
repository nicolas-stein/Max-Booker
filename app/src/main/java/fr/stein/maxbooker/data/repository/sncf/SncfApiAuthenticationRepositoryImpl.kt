package fr.stein.maxbooker.data.repository.sncf

import androidx.datastore.core.DataStore
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.data.mapper.toProto
import fr.stein.maxbooker.domain.model.sncf.SncfApiAuthentication
import fr.stein.maxbooker.domain.repository.sncf.SncfApiAuthenticationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SncfApiAuthenticationRepositoryImpl(
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
}
