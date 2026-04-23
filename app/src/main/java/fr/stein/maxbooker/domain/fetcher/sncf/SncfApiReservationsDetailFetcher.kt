package fr.stein.maxbooker.domain.fetcher.sncf

import android.util.Log
import fr.stein.maxbooker.data.exception.SncfApiException
import fr.stein.maxbooker.domain.fetcher.DataState
import fr.stein.maxbooker.domain.model.sncf.customer.SncfCustomer
import fr.stein.maxbooker.domain.model.sncf.reservation.SncfReservation
import fr.stein.maxbooker.domain.usecase.SncfApiFetchReservationDetailUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SncfApiReservationsDetailFetcher @Inject constructor(
    private val sncfApiFetchReservationDetailUseCase: SncfApiFetchReservationDetailUseCase
) {
    private val _reservationsState =
        MutableStateFlow<MutableMap<String, DataState<SncfReservation>>>(mutableMapOf())
    val reservationsState = _reservationsState.asStateFlow()

    suspend fun fetchReservationDetail(customer: SncfCustomer, sncfReservation: SncfReservation) {
        if (_reservationsState.value[sncfReservation.dvNumber] is DataState.Loading) return

        _reservationsState.value[sncfReservation.dvNumber] = DataState.Loading
        Log.d("Max Book", "SncfApiReservationsFetcher: fetching reservations...")

        runCatching {
            return@runCatching sncfApiFetchReservationDetailUseCase(customer, sncfReservation)
        }.onSuccess { result ->
            _reservationsState.value[sncfReservation.dvNumber] = DataState.Success(result)
        }.onFailure { throwable ->
            when (throwable) {
                is SncfApiException.NetworkException ->
                    _reservationsState.value[sncfReservation.dvNumber] =
                        DataState.Offline(null)
                else ->
                    _reservationsState.value[sncfReservation.dvNumber] =
                        DataState.Error(SncfApiException.UnexpectedException(throwable))
            }
            Log.e("Max Book", "SncfApiReservationsDetailFetcher: error fetching reservations detail", throwable)
        }
    }
}
