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
        Log.d(
            "Max Book",
            "SncfApiAuthenticator: triggered for route ${response.request.url}, refreshing authentication."
        )
        try {
            val authorizationHeader = synchronized(this) {
                runBlocking {
                    sncfApiAuthenticationRepository.get().refreshAuthentication()
                    sncfApiAuthenticationRepository.get().getAuthorizationHeader()
                }
            }

            if (authorizationHeader != null) {
                Log.d(
                    "Max Book",
                    "SncfApiAuthenticator: re-running request for ${response.request.url}"
                )
                return response.request.newBuilder()
                    .header("Authorization", authorizationHeader)
                    .build()
            } else {
                Log.e("Max Book", "SncfApiAuthenticator: newSncfApiAuthentication is null !")
                return null
            }
        } catch (_: SncfApiException) {
            return null
        }
    }
}
