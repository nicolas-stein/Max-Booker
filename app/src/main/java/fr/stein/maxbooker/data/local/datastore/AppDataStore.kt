package fr.stein.maxbooker.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import fr.stein.maxbooker.data.local.maxbookersettings.MaxBookerSettingsProto
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto

fun createMaxBookerSettingsDataStore(context: Context): DataStore<MaxBookerSettingsProto> = DataStoreFactory.create(
    serializer = MaxBookerSettingsSerializer,
    produceFile = { context.dataStoreFile("maxbooker_settings.pb") }
)

fun createSncfApiAuthenticationDataStore(context: Context): DataStore<SncfApiAuthenticationProto> =
    DataStoreFactory.create(
        serializer = SncfApiAuthenticationSerializer,
        produceFile = { context.dataStoreFile("sncf_api_authentication.pb") }
    )

fun createSncfCustomerDataStore(context: Context): DataStore<SncfCustomerProto> = DataStoreFactory.create(
    serializer = SncfCustomerSerializer,
    produceFile = { context.dataStoreFile("sncf_customer.pb") }
)
