package fr.stein.maxbooker.data.remote.sncf

import android.util.Log
import fr.stein.maxbooker.domain.repository.sncf.SncfApiAuthenticationRepository
import javax.inject.Provider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class SncfApiInterceptor(
    private val sncfApiAuthenticationRepository: Provider<SncfApiAuthenticationRepository>
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
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
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
