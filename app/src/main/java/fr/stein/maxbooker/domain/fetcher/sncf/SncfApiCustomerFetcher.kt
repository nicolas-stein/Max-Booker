package fr.stein.maxbooker.domain.fetcher.sncf

import android.util.Log
import androidx.datastore.core.DataStore
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.data.local.sncfcustomer.SncfCustomerProto
import fr.stein.maxbooker.data.mapper.toDomain
import fr.stein.maxbooker.data.mapper.toProto
import fr.stein.maxbooker.domain.fetcher.DataState
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomer
import fr.stein.maxbooker.domain.usecase.SncfApiFetchCustomerUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SncfApiCustomerFetcher @Inject constructor(
    private val sncfApiFetchCustomerUseCase: SncfApiFetchCustomerUseCase,
    private val sncfCustomerDataStore: DataStore<SncfCustomerProto>
) {
    private val _customerState = MutableStateFlow<DataState<SncfCustomer>>(DataState.Offline(null))
    val customerState = _customerState.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            fetchCustomer()
        }
    }

    suspend fun fetchCustomer() {
        if (_customerState.value is DataState.Loading) return

        _customerState.value = DataState.Loading
        Log.d("Max Book", "SncfApiCustomerFetcher: fetching customer...")
        try {
            val sncfCustomer = sncfApiFetchCustomerUseCase(null)
            Log.d("Max Book", "SncfApiCustomerFetcher: successfully fetched customer.")
            sncfCustomerDataStore.updateData { sncfCustomer.toProto() }
            _customerState.value = DataState.Success(sncfCustomer)
        } catch (exception: SncfApiException) {
            if (exception is SncfApiException.NetworkException) {
                loadCustomerFromDataStore()
            } else {
                _customerState.value = DataState.Error(exception)
                Log.e(
                    "Max Book",
                    "SncfApiCustomerFetcher: error while fetching customer",
                    exception
                )
            }
        }
    }

    private suspend fun loadCustomerFromDataStore() {
        val sncfCustomer = sncfCustomerDataStore.data.first().toDomain()
        _customerState.value = DataState.Offline(sncfCustomer)
    }
}
