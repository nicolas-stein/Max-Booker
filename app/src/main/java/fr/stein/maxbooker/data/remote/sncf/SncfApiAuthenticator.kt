package fr.stein.maxbooker.data.remote.sncf

import android.util.Log
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.domain.repository.sncf.SncfApiAuthenticationRepository
import javax.inject.Provider
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class SncfApiAuthenticator(
    private val sncfApiAuthenticationRepository: Provider<SncfApiAuthenticationRepository>
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("Max Book", "SncfApiAuthenticator: triggered for route ${response.request.url}")
        try {
            synchronized(this) {
                runBlocking {
                    sncfApiAuthenticationRepository.get().refreshAuthentication()
                }
            }
            return response.request
        } catch (_: SncfApiException) {
            return null
        }
    }
}
