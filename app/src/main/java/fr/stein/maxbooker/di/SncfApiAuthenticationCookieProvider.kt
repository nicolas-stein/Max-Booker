package fr.stein.maxbooker.di

import androidx.datastore.core.DataStore
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SncfApiAuthenticationCookieProvider @Inject constructor(
    sncfApiAuthenticationDataStore: DataStore<SncfApiAuthenticationProto>
) {

    private val _cookieFlow = MutableStateFlow("")
    val cookieFlow: StateFlow<String> = _cookieFlow

    init {
        CoroutineScope(Dispatchers.IO).launch {
            sncfApiAuthenticationDataStore.data.collect { sncfApiAuthentication ->
                _cookieFlow.value = sncfApiAuthentication.cookies
            }
        }
    }

    fun getCookies(): String {
        return cookieFlow.value
    }
}