package fr.stein.maxbooker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.stein.maxbooker.data.local.database.AppDataBase
import fr.stein.maxbooker.data.local.database.SncfReservationDao
import fr.stein.maxbooker.data.local.datastore.createMaxBookerSettingsDataStore
import fr.stein.maxbooker.data.local.datastore.createSncfApiAuthenticationDataStore
import fr.stein.maxbooker.data.local.datastore.createSncfCustomerDataStore
import fr.stein.maxbooker.data.local.maxbookersettings.MaxBookerSettingsProto
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDataModules {
    @Provides
    @Singleton
    fun provideMaxBookerSettingsDataStore(@ApplicationContext context: Context): DataStore<MaxBookerSettingsProto> =
        createMaxBookerSettingsDataStore(context)

    @Provides
    @Singleton
    fun provideSncfApiAuthenticationDataStore(
        @ApplicationContext context: Context
    ): DataStore<SncfApiAuthenticationProto> = createSncfApiAuthenticationDataStore(context)

    @Provides
    @Singleton
    fun provideSncfCustomerDataStore(@ApplicationContext context: Context): DataStore<SncfCustomerProto> =
        createSncfCustomerDataStore(context)

    @Provides
    @Singleton
    fun provideAppDataBase(@ApplicationContext context: Context): AppDataBase = Room.databaseBuilder(
        context,
        AppDataBase::class.java,
        "maxbook-db"
    ).build()

    @Provides
    @Singleton
    fun provideSncfReservationDao(appDataBase: AppDataBase): SncfReservationDao = appDataBase.sncfReservationDao()
}
