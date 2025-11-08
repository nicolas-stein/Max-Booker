package fr.stein.maxbooker.data.remote.sncf

import android.content.Context
import android.util.Log
import android.webkit.WebSettings
import fr.stein.maxbooker.domain.repository.sncf.SncfApiAuthenticationRepository
import javax.inject.Provider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class SncfApiInterceptor(
    private val sncfApiAuthenticationRepository: Provider<SncfApiAuthenticationRepository>,
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val original = chain.request()
        Log.d("Max Book", "SncfApiInterceptor: intercepting request to ${original.url}")

        val requestBuilder = original.newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Accept-Language", "en-US")
            .addHeader("Content-Type", "application/json")
            .addHeader(
                "User-Agent",
                WebSettings.getDefaultUserAgent(context)
            )
            .addHeader("x-client-app", "MAX_JEUNE")

        if (original.header("Cookie") == null) {
            requestBuilder.addHeader(
                "Cookie",
                sncfApiAuthenticationRepository.get().getSncfApiAuthentication()?.cookies ?: ""
            )
        }
        chain.proceed(requestBuilder.build())
    }
}
