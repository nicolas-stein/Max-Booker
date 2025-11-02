package fr.stein.maxbooker.di

import androidx.datastore.core.DataStore
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.stein.maxbooker.data.local.database.SncfReservationDao
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto
import fr.stein.maxbooker.data.remote.sncf.SncfApi
import fr.stein.maxbooker.data.remote.sncf.SncfApiAuthenticator
import fr.stein.maxbooker.data.remote.sncf.SncfApiInterceptor
import fr.stein.maxbooker.data.repository.sncf.SncfApiAuthenticationRepositoryImpl
import fr.stein.maxbooker.data.repository.sncf.SncfApiExecutorImpl
import fr.stein.maxbooker.data.repository.sncf.SncfApiRepositoryImpl
import fr.stein.maxbooker.domain.fetcher.sncf.SncfApiCustomerFetcher
import fr.stein.maxbooker.domain.fetcher.sncf.SncfApiReservationsDetailFetcher
import fr.stein.maxbooker.domain.fetcher.sncf.SncfApiReservationsFetcher
import fr.stein.maxbooker.domain.repository.sncf.SncfApiAuthenticationRepository
import fr.stein.maxbooker.domain.repository.sncf.SncfApiExecutor
import fr.stein.maxbooker.domain.repository.sncf.SncfApiRepository
import fr.stein.maxbooker.domain.usecase.SncfApiFetchCustomerUseCase
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Provider
import javax.inject.Singleton

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
            .authenticator(SncfApiAuthenticator(sncfApiAuthenticationRepository))
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
        sncfApi: SncfApi,
        sncfApiAuthenticationDataStore: DataStore<SncfApiAuthenticationProto>
    ): SncfApiAuthenticationRepository = SncfApiAuthenticationRepositoryImpl(
        sncfApi = sncfApi,
        sncfApiAuthenticationDataStore = sncfApiAuthenticationDataStore
    )

    @Provides
    @Singleton
    fun provideSncfApiCustomerFetcher(
        sncfApiFetchCustomerUseCase: SncfApiFetchCustomerUseCase,
        sncfCustomerDataStore: DataStore<SncfCustomerProto>
    ): SncfApiCustomerFetcher =
        SncfApiCustomerFetcher(sncfApiFetchCustomerUseCase, sncfCustomerDataStore)

    @Provides
    @Singleton
    fun provideSncfApiReservationsFetcher(
        sncfApiRepository: SncfApiRepository,
        sncfApiCustomerFetcher: SncfApiCustomerFetcher,
        sncfApiReservationsDetailFetcher: SncfApiReservationsDetailFetcher,
        sncfReservationDao: SncfReservationDao,
        @AppModules.ApplicationScope applicationScope: CoroutineScope
    ): SncfApiReservationsFetcher = SncfApiReservationsFetcher(
        sncfApiRepository = sncfApiRepository,
        sncfApiCustomerFetcher = sncfApiCustomerFetcher,
        sncfApiReservationsDetailFetcher = sncfApiReservationsDetailFetcher,
        sncfReservationDao = sncfReservationDao,
        applicationScope = applicationScope
    )

    @Provides
    @Singleton
    fun provideSncfApiReservationsDetailFetcher(
        sncfApiRepository: SncfApiRepository,
        sncfReservationDao: SncfReservationDao
    ): SncfApiReservationsDetailFetcher = SncfApiReservationsDetailFetcher(
        sncfApiRepository = sncfApiRepository,
        sncfReservationDao = sncfReservationDao
    )
}
