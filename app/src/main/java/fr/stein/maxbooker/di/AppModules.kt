package fr.stein.maxbooker.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.stein.maxbooker.data.local.database.SncfReservationDao
import fr.stein.maxbooker.data.local.maxbookersettings.MaxBookerSettingsProto
import fr.stein.maxbooker.data.repository.maxbooker.MaxBookerSettingsRepositoryImpl
import fr.stein.maxbooker.domain.repository.sncf.MaxBookerSettingsRepository
import fr.stein.maxbooker.domain.utils.WorkerScheduler
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object AppModules {
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class ApplicationScope

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideObjectMapper(): ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    @Provides
    @Singleton
    fun provideMaxBookerSettingsRepository(
        maxBookerSettingsDataStore: DataStore<MaxBookerSettingsProto>
    ): MaxBookerSettingsRepository = MaxBookerSettingsRepositoryImpl(maxBookerSettingsDataStore)

    @Provides
    @Singleton
    fun provideWorkScheduler(
        @ApplicationContext context: Context,
        maxBookerSettingsRepository: MaxBookerSettingsRepository,
        sncfReservationDao: SncfReservationDao
    ): WorkerScheduler = WorkerScheduler(context, maxBookerSettingsRepository, sncfReservationDao)
}
