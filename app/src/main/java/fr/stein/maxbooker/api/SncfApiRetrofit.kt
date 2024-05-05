package fr.stein.maxbooker.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


fun getRetrofit(authorizationToken: String?, cookieHeader: String): Retrofit {

    val httpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val original = chain.request()

        val requestBuilder = original.newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Accept-Language", "en-US")
            .addHeader("Content-Type", "application/json")
            .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
            .addHeader("Cookie", cookieHeader)
            .addHeader("x-client-app", "MAX_JEUNE")

        if(authorizationToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer $authorizationToken")
        }

        chain.proceed(requestBuilder.build())
    }.build()

    return Retrofit.Builder()
        .baseUrl("https://www.maxjeune-tgvinoui.sncf/api/public/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun getSncfApi(authorizationToken: String?, cookieHeader: String): SncfApi {
    return getRetrofit(authorizationToken, cookieHeader).create(SncfApi::class.java)
}