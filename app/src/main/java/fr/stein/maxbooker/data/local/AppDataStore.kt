package fr.stein.maxbooker.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationProto
import fr.stein.maxbooker.data.local.sncfapiauthentication.SncfApiAuthenticationSerializer
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerSerializer

fun createSncfApiAuthenticationDataStore(context: Context): DataStore<SncfApiAuthenticationProto> =
    DataStoreFactory.create(
        serializer = SncfApiAuthenticationSerializer,
        produceFile = { context.dataStoreFile("sncf_api_authentication.pb") }
    )

fun createSncfCustomerDataStore(context: Context): DataStore<SncfCustomerProto> =
    DataStoreFactory.create(
        serializer = SncfCustomerSerializer,
        produceFile = { context.dataStoreFile("sncf_customer.pb") }
    )
