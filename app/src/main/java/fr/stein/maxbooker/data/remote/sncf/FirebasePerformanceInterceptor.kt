package fr.stein.maxbooker.data.remote.sncf

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.HttpMetric
import okhttp3.Interceptor
import okhttp3.Response

class FirebasePerformanceInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        val metric: HttpMetric = FirebasePerformance.getInstance()
            .newHttpMetric(url, request.method)

        request.body?.contentLength()?.let { size ->
            metric.setRequestPayloadSize(size)
        }

        metric.start()

        return try {
            val response = chain.proceed(request)

            metric.setHttpResponseCode(response.code)
            response.body?.contentLength()?.let { size ->
                metric.setResponsePayloadSize(size)
            }

            if (!response.isSuccessful) {
                // Clone response body to read it safely
                val errorBody = response.peekBody(Long.MAX_VALUE).string()

                metric.putAttribute(
                    "response_error",
                    errorBody.take(100) // Firebase limit
                )
            }

            response
        } catch (e: Exception) {
            metric.setHttpResponseCode(0)
            metric.putAttribute(
                "request_error",
                e.localizedMessage?.take(100) ?: "unknown"
            )

            throw e
        } finally {
            metric.stop()
        }
    }
}
