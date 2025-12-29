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

class SncfApiAuthenticator(private val sncfApiAuthenticationRepository: Provider<SncfApiAuthenticationRepository>) :
    Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val failedCookie = response.request.header("Cookie")
        synchronized(this) {
            return runBlocking {
                val latestCookie = sncfApiAuthenticationRepository.get().getSncfApiAuthentication()?.cookies
                if (latestCookie != null && failedCookie != latestCookie) {
                    Log.d(
                        "Max Book",
                        "SncfApiAuthenticator: triggered for route ${response.request.url}, already re-authenticated. Re-running request for ${response.request.url}"
                    )
                    return@runBlocking response.request.newBuilder()
                        .header("Cookie", latestCookie)
                        .build()
                }

                Log.d(
                    "Max Book",
                    "SncfApiAuthenticator: triggered for route ${response.request.url}, refreshing authentication."
                )

                val cookies = sncfApiAuthenticationRepository.get().getSncfApiAuthentication()?.cookies
                if (cookies == null) {
                    Log.d("Max Book", "SncfApiAuthenticator: no cookies, cannot refresh authentication!")
                    return@runBlocking null
                }

                try {
                    val newCookies =
                        sncfApiAuthenticationRepository.get().refreshAuthenticationCookie(
                            cookies
                        )?.cookies

                    if (newCookies != null) {
                        Log.d(
                            "Max Book",
                            "SncfApiAuthenticator: re-running request for ${response.request.url}"
                        )
                        return@runBlocking response.request.newBuilder()
                            .header("Cookie", newCookies)
                            .build()
                    } else {
                        Log.e("Max Book", "SncfApiAuthenticator: newCookies is null !")
                        return@runBlocking null
                    }
                } catch (_: SncfApiException) {
                    return@runBlocking null
                }
            }
        }
    }
}
