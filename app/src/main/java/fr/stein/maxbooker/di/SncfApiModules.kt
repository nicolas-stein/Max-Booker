package fr.stein.maxbooker.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto
import fr.stein.maxbooker.data.remote.sncf.FirebasePerformanceInterceptor
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
import fr.stein.maxbooker.domain.usecase.SncfApiFetchReservationDetailUseCase
import fr.stein.maxbooker.domain.usecase.SncfApiFetchReservationsUseCase
import javax.inject.Provider
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object SncfApiModules {

    @Provides
    @Singleton
    fun provideSncfApi(
        sncfApiAuthenticationRepository: Provider<SncfApiAuthenticationRepository>,
        objectMapper: ObjectMapper,
        @ApplicationContext context: Context
    ): SncfApi {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(FirebasePerformanceInterceptor())
            .addInterceptor(SncfApiInterceptor(sncfApiAuthenticationRepository, context))
            .authenticator(SncfApiAuthenticator(sncfApiAuthenticationRepository))
            .build()

        return Retrofit.Builder()
            .baseUrl("https://www.maxjeune-tgvinoui.sncf/api/public/")
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .client(httpClient)
            .build()
            .create(SncfApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSncfApiRepository(sncfApi: SncfApi, sncfApiExecutor: SncfApiExecutor): SncfApiRepository =
        SncfApiRepositoryImpl(
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
    ): SncfApiCustomerFetcher = SncfApiCustomerFetcher(sncfApiFetchCustomerUseCase, sncfCustomerDataStore)

    @Provides
    @Singleton
    fun provideSncfApiReservationsFetcher(
        sncfApiFetchReservationsUseCase: SncfApiFetchReservationsUseCase,
        sncfApiCustomerFetcher: SncfApiCustomerFetcher,
        sncfApiReservationsDetailFetcher: SncfApiReservationsDetailFetcher,
        @AppModules.ApplicationScope applicationScope: CoroutineScope
    ): SncfApiReservationsFetcher = SncfApiReservationsFetcher(
        sncfApiFetchReservationsUseCase = sncfApiFetchReservationsUseCase,
        sncfApiCustomerFetcher = sncfApiCustomerFetcher,
        sncfApiReservationsDetailFetcher = sncfApiReservationsDetailFetcher,
        applicationScope = applicationScope
    )

    @Provides
    @Singleton
    fun provideSncfApiReservationsDetailFetcher(
        sncfApiFetchReservationDetailUseCase: SncfApiFetchReservationDetailUseCase
    ): SncfApiReservationsDetailFetcher = SncfApiReservationsDetailFetcher(
        sncfApiFetchReservationDetailUseCase = sncfApiFetchReservationDetailUseCase
    )
}
