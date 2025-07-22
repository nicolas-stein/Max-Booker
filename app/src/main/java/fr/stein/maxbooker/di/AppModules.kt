package fr.stein.maxbooker.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.stein.maxbooker.data.local.createSncfApiTokenDataStore
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.remote.SncfApi
import fr.stein.maxbooker.data.repository.SncfApiRepositoryImpl
import fr.stein.maxbooker.domain.repository.SncfApiRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModules {

    @Provides
    @Singleton
    fun provideSncfApi(cookieProvider: SncfApiAuthenticationCookieProvider): SncfApi {
        val httpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Accept-Language", "en-US")
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                .addHeader("x-client-app", "MAX_JEUNE")

            if (original.header("Cookie") == null) {
                requestBuilder.addHeader("Cookie", cookieProvider.getCookies())
            }

            chain.proceed(requestBuilder.build())
        }.build()

        return Retrofit.Builder()
            .baseUrl("https://www.maxjeune-tgvinoui.sncf/api/public/")
            .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
            .client(httpClient)
            .build()
            .create(SncfApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSncfApiTokenDataStore(@ApplicationContext context: Context): DataStore<SncfApiAuthenticationProto> =
        createSncfApiTokenDataStore(context)

    @Provides
    @Singleton
    fun provideSncfApiRepository(sncfApi: SncfApi, sncfApiAuthenticationDataStore: DataStore<SncfApiAuthenticationProto>): SncfApiRepository =
        SncfApiRepositoryImpl(sncfApi, sncfApiAuthenticationDataStore)
}