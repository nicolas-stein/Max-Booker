package fr.stein.maxbooker.domain.fetcher.sncf

import android.util.Log
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.domain.fetcher.DataState
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.usecase.SncfApiFetchReservationsUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Singleton
class SncfApiReservationsFetcher @Inject constructor(
    private val sncfApiFetchReservationsUseCase: SncfApiFetchReservationsUseCase,
    sncfApiCustomerFetcher: SncfApiCustomerFetcher,
    private val sncfApiReservationsDetailFetcher: SncfApiReservationsDetailFetcher,
    private val applicationScope: CoroutineScope
) {
    private val _reservationsState =
        MutableStateFlow<DataState<List<SncfReservation>>>(DataState.Offline(null))
    val reservationsState = _reservationsState.asStateFlow()

    init {
        sncfApiCustomerFetcher.customerState
            .onEach { dataState ->
                if (dataState is DataState.Success) {
                    applicationScope.launch(Dispatchers.IO) {
                        fetchReservations(dataState.data)
                    }
                }
            }
            .launchIn(applicationScope)
    }

    private suspend fun fetchReservations(customer: SncfCustomer) {
        if (_reservationsState.value is DataState.Loading) return

        _reservationsState.value = DataState.Loading
        Log.d("Max Book", "SncfApiReservationsFetcher: fetching reservations...")

        runCatching {
            return@runCatching sncfApiFetchReservationsUseCase(customer)
        }.onSuccess { output ->
            val sncfReservations = output.newSncfReservations + output.updatedSncfReservations
            _reservationsState.value = DataState.Success(sncfReservations)
            sncfReservations.forEach { sncfReservation ->
                sncfApiReservationsDetailFetcher.fetchReservationDetail(
                    customer,
                    sncfReservation
                )
            }
        }.onFailure { throwable ->
            when (throwable) {
                is SncfApiException.NetworkException ->
                    _reservationsState.value =
                        DataState.Offline(null)
                else ->
                    _reservationsState.value =
                        DataState.Error(SncfApiException.UnexpectedException(throwable))
            }
            Log.e("Max Book", "SncfApiReservationsFetcher: error fetching reservations", throwable)
        }
    }
}
