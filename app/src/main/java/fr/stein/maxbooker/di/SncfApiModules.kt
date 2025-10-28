package fr.stein.maxbooker.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.stein.maxbooker.data.local.createSncfApiAuthenticationDataStore
import fr.stein.maxbooker.data.local.createSncfCustomerDataStore
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto
import fr.stein.maxbooker.data.remote.sncf.SncfApi
import fr.stein.maxbooker.data.remote.sncf.SncfApiInterceptor
import fr.stein.maxbooker.data.repository.sncf.SncfApiAuthenticationRepositoryImpl
import fr.stein.maxbooker.data.repository.sncf.SncfApiExecutorImpl
import fr.stein.maxbooker.data.repository.sncf.SncfApiRepositoryImpl
import fr.stein.maxbooker.domain.fetcher.sncf.SncfApiCustomerFetcher
import fr.stein.maxbooker.domain.repository.sncf.SncfApiAuthenticationRepository
import fr.stein.maxbooker.domain.repository.sncf.SncfApiExecutor
import fr.stein.maxbooker.domain.repository.sncf.SncfApiRepository
import fr.stein.maxbooker.domain.usecase.SncfApiFetchCustomerUseCase
import javax.inject.Provider
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object SncfApiModules {

    @Provides
    @Singleton
    fun provideSncfApi(
        sncfApiAuthenticationRepository: Provider<SncfApiAuthenticationRepository>
    ): SncfApi {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(SncfApiInterceptor(sncfApiAuthenticationRepository))
            .build()

        return Retrofit.Builder()
            .baseUrl("https://www.maxjeune-tgvinoui.sncf/api/public/")
            .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
            .client(httpClient)
            .build()
            .create(SncfApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSncfApiAuthenticationDataStore(
        @ApplicationContext context: Context
    ): DataStore<SncfApiAuthenticationProto> = createSncfApiAuthenticationDataStore(context)

    @Provides
    @Singleton
    fun provideSncfCustomerDataStore(
        @ApplicationContext context: Context
    ): DataStore<SncfCustomerProto> = createSncfCustomerDataStore(context)

    @Provides
    @Singleton
    fun provideSncfApiRepository(
        sncfApi: SncfApi,
        sncfApiExecutor: SncfApiExecutor
    ): SncfApiRepository = SncfApiRepositoryImpl(
        sncfApi,
        sncfApiExecutor
    )

    @Provides
    @Singleton
    fun provideSncfApiExecutor(
        sncfApiAuthenticationRepository: Provider<SncfApiAuthenticationRepository>
    ): SncfApiExecutor = SncfApiExecutorImpl(sncfApiAuthenticationRepository)

    @Provides
    @Singleton
    fun provideSncfApiAuthenticationRepository(
        sncfApiAuthenticationDataStore: DataStore<SncfApiAuthenticationProto>
    ): SncfApiAuthenticationRepository = SncfApiAuthenticationRepositoryImpl(
        sncfApiAuthenticationDataStore = sncfApiAuthenticationDataStore
    )

    @Provides
    @Singleton
    fun provideSncfApiCustomerFetcher(
        sncfApiFetchCustomerUseCase: SncfApiFetchCustomerUseCase,
        sncfCustomerDataStore: DataStore<SncfCustomerProto>
    ): SncfApiCustomerFetcher =
        SncfApiCustomerFetcher(sncfApiFetchCustomerUseCase, sncfCustomerDataStore)
}
