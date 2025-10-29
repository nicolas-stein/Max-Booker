package fr.stein.maxbooker.data.remote.sncf

import android.util.Log
import android.webkit.CookieManager
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

        val cookies = CookieManager.getInstance().getCookie("https://www.maxjeune-tgvinoui.sncf/")
        if (cookies == null) {
            Log.d("Max Book", "SncfApiAuthenticator: no cookies, cannot refresh authentication!")
            return null
        }

        try {
            val authorizationHeader = synchronized(this) {
                runBlocking {
                    sncfApiAuthenticationRepository.get().refreshAuthenticationCookie(
                        cookies
                    )?.cookies
                }
            }

            if (authorizationHeader != null) {
                Log.d(
                    "Max Book",
                    "SncfApiAuthenticator: re-running request for ${response.request.url}"
                )
                return response.request.newBuilder()
                    .header("Cookie", authorizationHeader)
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
