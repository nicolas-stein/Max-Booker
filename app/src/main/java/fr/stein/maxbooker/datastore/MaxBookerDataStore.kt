package fr.stein.maxbooker.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import fr.stein.maxbooker.proto.AppSettings
import fr.stein.maxbooker.proto.SncfApiSettings
import fr.stein.maxbooker.proto.SncfUser

class MaxBookerDataStore(private val context: Context) {
    companion object {
        val Context.sncfApiSettingsDatastore: DataStore<SncfApiSettings> by dataStore(
            fileName = "sncf_settings.pb",
            serializer = SncfApiSettingsSerializer()
        )

        val Context.sncfApiUser: DataStore<SncfUser> by dataStore(
            fileName = "sncf_user.pb",
            serializer = SncfUserSerializer()
        )

        val Context.appSettings: DataStore<AppSettings> by dataStore(
            fileName = "app_settings.pb",
            serializer = AppSettingsSerializer()
        )
    }

    fun getSncfApiSettingsDatastore(): DataStore<SncfApiSettings> {
        return context.sncfApiSettingsDatastore
    }

    fun getSncfUserDatastore(): DataStore<SncfUser> {
        return context.sncfApiUser
    }

    fun getAppSettingsDatastore(): DataStore<AppSettings> {
        return context.appSettings
    }
}